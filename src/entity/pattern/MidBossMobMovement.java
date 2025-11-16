package entity.pattern;

import java.util.List;

/**
 * Defines the movement strategy for MidBossMob child entities.
 * Implements two distinct movement patterns based on the boss's health.
 */
public class MidBossMobMovement {

    /** The width of the screen, used for wall collision checks. */
    private int wallWidth;
    /** The vertical boundary the child ships cannot cross. */
    private int bottomHeight;
    /** Reference to the list of active child ships. */
    private List<MidBossMob> ChildShips;
    /** Base movement speed of the child ships. */
    private int speed;
    /** Frame counter used for generating oscillating and orbital movement. */
    private int frameCount = 0;
    /** The width of the boss, used for center calculation. */
    private int MidBossWidth;
    /** The height of the boss, used for center calculation. */
    private int MidBossHeight;
    /** Radius of the orbit movement when in Pattern 2 or low HP state. */
    private static final double ORBIT_RADIUS = 50.0;
    /** Speed multiplier for the orbit angle per frame. */
    private static final double ORBIT_SPEED = 0.02;
    /** Frequency for horizontal oscillation when descending. */
    private static final double OSCILLATION_FREQUENCY = 0.1;
    /** Margin distance from the bottom boundary used for descent stop. */
    private static final int Bottom_Margin = 50;
    /**
     * Initializes the movement strategy parameters.
     * @param ITEMS_SEPARATION_LINE_HEIGHT The lower boundary of the play area.
     * @param screenWidth The total screen width.
     * @param bossWidth The width of the main boss.
     * @param bossHeight The height of the main boss.
     * @param childSpeed The base speed for child movement.
     */
    public MidBossMobMovement(int ITEMS_SEPARATION_LINE_HEIGHT , int screenWidth, int bossWidth, int bossHeight, int childSpeed) {
        this.bottomHeight = ITEMS_SEPARATION_LINE_HEIGHT;
        this.wallWidth = screenWidth;
        this.MidBossWidth = bossWidth;
        this.MidBossHeight = bossHeight;
        this.speed = childSpeed;
    }
    /**
     * Calculates the vector to move the ship toward a target position.
     * Includes a speed boost for long distances and prevents overshooting the target.
     * @param ship The child entity to move.
     * @param targetX The target X coordinate.
     * @param targetY The target Y coordinate.
     */
    private void moveToTarget(MidBossMob ship, int targetX, int targetY) {
        double dx = targetX - ship.getPositionX();
        double dy = targetY - ship.getPositionY();
        double distance = Math.sqrt(dx*dx + dy*dy);
        double speedMultiplier = (distance > 10) ? 10.0 : 1.0;
        // Calculate movement based on unit vector * speed * multiplier
        int moveX = (int) (dx * this.speed / distance * speedMultiplier);
        int moveY = (int) (dy * this.speed / distance * speedMultiplier);
        // Overshoot prevention: Ensure the move distance does not exceed the remaining distance
        if (Math.abs(moveX) > Math.abs(dx)) moveX = (int) dx;
        if (Math.abs(moveY) > Math.abs(dy)) moveY = (int) dy;

        ship.move(moveX, moveY);

    }
    /**
     * Executes Movement Pattern 1.
     * HP > 50%: Side-to-side wall bouncing.
     * HP <= 50%: Orbit movement around the boss.
     * @param bossX The X position of the main boss.
     * @param bossY The Y position of the main boss.
     * @param Boss_Delta_Childs The list of child entities.
     * @param initialChildCount The total number of children spawned
     */
    public void pattern_1_Movement(int bossX, int bossY, List<MidBossMob> Boss_Delta_Childs, int initialChildCount){
        this.frameCount++;
        this.ChildShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.MidBossWidth /2;
        int bossCenterY = bossY + this.MidBossHeight/2;


        for(int childIndex = 0; childIndex < ChildShips.size(); childIndex++){
            MidBossMob child = ChildShips.get(childIndex);
            if(child.getHealPoint() > child.getMaxHp()*0.5){
                if(child.getDirectionRight()){
                    boolean isRightWall = child.getPositionX() + child.getWidth() + this.speed > this.wallWidth;
                    if(isRightWall){ child.setDirectionRight(false); }
                    else { child.move(this.speed,0); }
                } else {
                    boolean isLeftWall = child.getPositionX() - this.speed < 0;
                    if(isLeftWall){ child.setDirectionRight(true); }
                    else { child.move(-this.speed,0); }
                }
            }else {
                double radius = this.ORBIT_RADIUS + (this.ORBIT_RADIUS / initialChildCount);
                double angle = (this.frameCount * this.ORBIT_SPEED) + (childIndex * 2 * Math.PI / initialChildCount);

                int orbitTargetX = (int) (bossCenterX + Math.cos(angle) * radius);
                int orbitTargetY = (int) (bossCenterY + Math.sin(angle) * radius);

                int finalTargetX = orbitTargetX - child.getWidth()/ 2;
                int finalTargetY = orbitTargetY - child.getHeight()/ 2;

                boolean isTopWall = finalTargetY < 50;
                boolean isBottomWall = finalTargetY + child.getHeight() > bottomHeight;
                if(isTopWall){finalTargetY = 51;}
                if(isBottomWall){finalTargetY = bottomHeight - child.getHeight();}
                moveToTarget(child, finalTargetX, finalTargetY);
            }
        }
    }

    /**
     * Executes Movement Pattern 2.
     * HP > 50%: Orbit movement around the boss.
     * HP <= 50%: Descent with horizontal sine wave oscillation, stopping at the bottom margin.
     * @param bossX The X position of the main boss.
     * @param bossY The Y position of the main boss.
     * @param Boss_Delta_Childs The list of child entities.
     * @param initialChildCount The total number of children spawned.
     */
    public void pattern_2_Movement(int bossX, int bossY, List<MidBossMob> Boss_Delta_Childs, int initialChildCount){
        this.frameCount++;
        this.ChildShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.MidBossWidth /2;
        int bossCenterY = bossY + this.MidBossHeight/2;

        for(int childIndex = 0; childIndex < this.ChildShips.size(); childIndex++){
            MidBossMob child = this.ChildShips.get(childIndex);
            if(child.getHealPoint() > child.getMaxHp()*0.5){
                double radius = this.ORBIT_RADIUS + (this.ORBIT_RADIUS / initialChildCount);
                double angle = (this.frameCount * this.ORBIT_SPEED) + (childIndex * 2 * Math.PI / initialChildCount);

                int orbitTargetX = (int) (bossCenterX + Math.cos(angle) * radius);
                int orbitTargetY = (int) (bossCenterY + Math.sin(angle) * radius);

                int finalTargetX = orbitTargetX - child.getWidth()/ 2;
                int finalTargetY = orbitTargetY - child.getHeight()/ 2;

                boolean isTopWall = finalTargetY < 50;
                boolean isBottomWall = finalTargetY + child.getHeight() > bottomHeight;
                if(isTopWall){finalTargetY = 51;}
                if(isBottomWall){finalTargetY = bottomHeight - child.getHeight();}
                moveToTarget(child, finalTargetX, finalTargetY);
            }else {
                boolean isBottom = child.getPositionY() + child.getHeight() + this.speed > this.bottomHeight - this.Bottom_Margin;
                int moveX = (int) (Math.cos(this.frameCount * this.OSCILLATION_FREQUENCY) * (this.speed * 3));
                if(isBottom){ child.move(moveX,0);}
                else {
                    int moveY = this.speed;
                    child.move(moveX, moveY);
                }

            }
        }
    }
}
