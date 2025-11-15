package entity;

import engine.DrawManager;

import java.awt.*;


public class MidBossMob extends MidBoss {
    private static int WIDTH = 24;
    private static int HEIGHT= 16;
    private boolean directionRight = true;

    public MidBossMob(int INIT_POS_X, int INIT_POS_Y, int healthPoint, int pointValue, Color color) {
        super(INIT_POS_X, INIT_POS_Y, WIDTH, HEIGHT, healthPoint, pointValue, color);
        this.spriteType = DrawManager.SpriteType.EnemyShipB1;
    }
    @Override
    public void move(int distanceX, int distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    @Override
    public void update() {
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

    public void destroy() {
        this.isDestroyed = true;
        this.spriteType = DrawManager.SpriteType.Explosion;
        this.logger.info("Child Destroyed");
    }

    @Override
    public void draw(DrawManager drawManager) {drawManager.getEntityRenderer().drawEntity(this,this.positionX,this.positionY);}

    @Override
    public int getHealPoint() { return this.healPoint; }

    @Override
    public int getPointValue() { return this.pointValue; }

    @Override
    public boolean isDestroyed() { return this.isDestroyed; }

    public int getMaxHp() { return this.maxHp; }

    public boolean getDirectionRight() { return this.directionRight; }

    public void setDirectionRight(boolean directionRightValue) { this.directionRight = directionRightValue; }

}
