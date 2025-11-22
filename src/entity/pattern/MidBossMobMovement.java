package entity.pattern;

import entity.MidBossMob;

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
    private List<MidBossMob> childShips;
    /** Base movement speed of the child ships. */
    private int speed;
    /** Frame counter used for generating oscillating and orbital movement. */
    private int frameCount = 0;
    /** The width of the boss, used for center calculation. */
    private int midBossWidth;
    /** The height of the boss, used for center calculation. */
    private int midBossHeight;
    /** Radius of the orbit movement when in Pattern 2 or low HP state. */
    private static final double ORBIT_RADIUS = 50.0;
    /** Speed multiplier for the orbit angle per frame. */
    private static final double ORBIT_SPEED = 0.02;
    /** Frequency for horizontal oscillation when descending. */
    private static final double OSCILLATION_FREQUENCY = 0.1;
    /** Multiplier to increase the speed of the horizontal sine wave movement. */
    private static final int OSCILLATION_FREQUENCY_MULTIPLIER = 3;
    /** Distance required to trigger an acceleration towards the target. */
    private static final double DISTANCE_THRESHOLD_FOR_SPEED_BOOST = 10.0;
    /** The highest Y-coordinate (ceiling) the child ships can move up to. */
    private static final int TOP_MARGIN = 80;
    /** Margin distance from the bottom boundary used for descent stop. */
    private static final int BOTTOM_MARGIN = 50;
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
        this.midBossWidth = bossWidth;
        this.midBossHeight = bossHeight;
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
        double speedMultiplier = (distance > 10) ? DISTANCE_THRESHOLD_FOR_SPEED_BOOST : 1.0;
        // Calculate movement based on unit vector * speed * multiplier
        int moveX = (int) (dx * this.speed / distance * speedMultiplier);
        int moveY = (int) (dy * this.speed / distance * speedMultiplier);
        // Overshoot prevention: Ensure the move distance does not exceed the remaining distance
        if (Math.abs(moveX) > Math.abs(dx)) moveX = (int) dx;
        if (Math.abs(moveY) > Math.abs(dy)) moveY = (int) dy;

        ship.move(moveX, moveY);

    }
    /**
     * Executes the circular orbit movement around the boss center for a single child ship.
     * Handles orbit calculation and clamping to the TOP_MARGIN and bottom boundary.
     * @param child The child entity to move.
     * @param childIndex The index of the child in the list, used to offset its orbit angle.
     * @param bossCenterX The center X position of the main boss.
     * @param bossCenterY The center Y position of the main boss.
     * @param initialChildCount The total number of children spawned, used to calculate orbit radius.
     */
    private void executeOrbitMovement(MidBossMob child, int childIndex, int bossCenterX, int bossCenterY, int initialChildCount) {
        double radius = this.ORBIT_RADIUS + (this.ORBIT_RADIUS / initialChildCount);
        double angle = (this.frameCount * this.ORBIT_SPEED) + (childIndex * 2 * Math.PI / initialChildCount);

        int orbitTargetX = (int) (bossCenterX + Math.cos(angle) * radius);
        int orbitTargetY = (int) (bossCenterY + Math.sin(angle) * radius);

        int finalTargetX = orbitTargetX - child.getWidth()/ 2;
        int finalTargetY = orbitTargetY - child.getHeight()/ 2;

        boolean isTopWall = finalTargetY < TOP_MARGIN;
        boolean isBottomWall = finalTargetY + child.getHeight() > bottomHeight;
        boolean isRightWall = finalTargetX + child.getWidth() > this.wallWidth;
        boolean isLeftWall = finalTargetX < 0;

        if(isTopWall){finalTargetY = TOP_MARGIN;}
        if(isBottomWall){finalTargetY = bottomHeight - child.getHeight();}
        if(isRightWall){finalTargetX = this.wallWidth - child.getWidth();}
        if(isLeftWall){finalTargetX = 0;}

        moveToTarget(child, finalTargetX, finalTargetY);
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
        this.childShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.midBossWidth /2;
        int bossCenterY = bossY + this.midBossHeight /2;


        for(int childIndex = 0; childIndex < childShips.size(); childIndex++){
            MidBossMob child = childShips.get(childIndex);
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
                executeOrbitMovement(child, childIndex, bossCenterX, bossCenterY, initialChildCount);
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
        this.childShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.midBossWidth /2;
        int bossCenterY = bossY + this.midBossHeight /2;

        for(int childIndex = 0; childIndex < this.childShips.size(); childIndex++){
            MidBossMob child = this.childShips.get(childIndex);
            if(child.getHealPoint() > child.getMaxHp()*0.5){
                executeOrbitMovement(child, childIndex, bossCenterX, bossCenterY, initialChildCount);
            }else {
                boolean isBottom = child.getPositionY() + child.getHeight() + this.speed > this.bottomHeight - this.BOTTOM_MARGIN;
                int moveX = (int) (Math.cos(this.frameCount * this.OSCILLATION_FREQUENCY) * (this.speed * OSCILLATION_FREQUENCY_MULTIPLIER));
                if(isBottom){ child.move(moveX,0);}
                else {
                    int moveY = this.speed;
                    child.move(moveX, moveY);
                }

            }
        }
    }
}
