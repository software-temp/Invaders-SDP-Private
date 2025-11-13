package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import engine.DrawManager;
import entity.EnemyShip;

public class DeltaBoss extends MidBoss {

    /** Initial position in the x-axis. */
    private static final int INIT_POS_X = 224;
    /** Initial position in the y-axis. */
    private static final int INIT_POS_Y = 50;
    /** Width of Omega */
    private static final int DELTA_WIDTH = 64;
    /** Height of Omega */
    private static final int DELTA_HEIGHT = 28;
    /** Current Health of Omega */
    private static final int DELTA_HEALTH = 100;
    /** Point of Omega when destroyed */
    private static final int DELTA_POINT_VALUE = 1000;
    /** Boss cannot move below this boundary. */
    private final int bottomBoundary;
    private final int screenWidth;
    /** Boss spawn child ships */
    private List<EnemyShip> ChildShips;
    private static final int ChildShip_Distance = 80;
    private int SPAWN_COUNT = 0;
    private int Child_Speed = 1;
    private Color[] colorPalette = {
            new Color( 0xFF4081),
            new Color( 0xFCDD8A),
            new Color( 0xFF5722),
            new Color( 0x8BC34A),
            new Color( 0x9C27B0),
            new Color( 0x6A89FF),
            new Color( 0x6756C9),
            new Color( 0xF2606F),
            new Color( 0xF5A5A5),
            new Color( 0x6F5E77),
            new Color( 0x32A9B3),
            new Color( 0x8303EE)
    };

    public DeltaBoss(Color color, int bottomBoundary, int width) {
        super(INIT_POS_X, INIT_POS_Y, DELTA_WIDTH, DELTA_HEIGHT, DELTA_HEALTH, DELTA_POINT_VALUE, color);
        this.bottomBoundary = bottomBoundary;
        this.screenWidth = width;
        this.spriteType = DrawManager.SpriteType.OmegaBoss1;
        this.ChildShips = new ArrayList<EnemyShip>();
    }


    @Override
    public void update() {
        this.spawnPattern();
        this.childMovePattern();
    }

    /** move simple */
    @Override
    public void move(int distanceX, int distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    /**
     * Reduces health and destroys the entity if it drops to zero or below.
     *
     * @param damage The amount of damage to inflict.
     */
    @Override
    public void takeDamage(int damage) {
        this.healPoint -= damage;
    }

    @Override
    public void destroy() {
        this.isDestroyed = true;
        this.spriteType = DrawManager.SpriteType.OmegaBossDeath;
        this.logger.info("Delta: Boss Delta Destroyed");
    }

    @Override
    public void draw(DrawManager drawManager) { drawManager.getEntityRenderer().drawEntity(this, this.positionX, this.positionY); }


    public void childMovePattern() {
        if(!this.ChildShips.isEmpty()) {
            for(EnemyShip ship : this.ChildShips) {
                if(ship.getDirection()== null || ship.getDirection() == EnemyShip.Direction.RIGHT){
                    boolean check_right_wall =ship.getPositionX() + ship.getWidth()+ship.getXSpeed() > screenWidth;
                    if(check_right_wall){ ship.setDirection(EnemyShip.Direction.LEFT); }
                    else{ ship.move(Child_Speed,0); }
                }else {
                    boolean check_left_wall =ship.getPositionX() < 0;
                    if(check_left_wall) { ship.setDirection(EnemyShip.Direction.RIGHT); }
                    else{ ship.move(-Child_Speed,0); }
                }
            }
        }
    }

    public void spawnPattern() {
        this.cleanDestroyedChild();
        if(SPAWN_COUNT == 0 && this.healPoint < this.maxHp*0.9){
            SPAWN_COUNT++;
            createChild(SPAWN_COUNT);
        } else if (SPAWN_COUNT == 1 && this.healPoint < this.maxHp*0.7){
            SPAWN_COUNT+=2;
            createChild(SPAWN_COUNT);
        } else if (SPAWN_COUNT == 3 && this.healPoint < this.maxHp*0.5){
            SPAWN_COUNT+=3;
            createChild(SPAWN_COUNT);
        } else if (SPAWN_COUNT == 6 && this.healPoint < this.maxHp*0.2){
            SPAWN_COUNT+=6;
            createChild(SPAWN_COUNT);
        }
    }

    public void createChild(int shipCount) {
        this.logger.info("Delta: Create Child");
        for (int count = 0; count < shipCount; count++) {
            EnemyShip ship = new EnemyShip(INIT_POS_X - 200 + ChildShip_Distance * count,
                    INIT_POS_Y + ChildShip_Distance, DrawManager.SpriteType.EnemyShipB1);
            ship.setColor(colorPalette[count]);
            this.ChildShips.add(ship);
        }

    }

    public void cleanDestroyedChild() {
        Iterator<EnemyShip> iterator = this.ChildShips.iterator();
        while(iterator.hasNext()){
            EnemyShip ship = iterator.next();
            if(ship.isDestroyed()) { iterator.remove(); }
        }
    }

    public List<EnemyShip> getChildShips() { return ChildShips; }
}