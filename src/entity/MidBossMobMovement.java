package entity;

import java.util.List;

public class MidBossMobMovement implements IMovementStrategy{

    private int wallWidth;
    private int bottomHeight;
    private List<MidBossMob> ChildShips;
    private int speed;
    private int frameCount = 0;
    private int DeltaBossWidth;
    private int DeltaBossHeight;
    private static final double ORBIT_RADIUS = 50.0;
    private static final double ORBIT_SPEED = 0.02;
    private static final double OSCILLATION_AMPLITUDE = 30.0;
    private static final double OSCILLATION_FREQUENCY = 0.1;
    private static final int Bottom_Margin = 50;

    public MidBossMobMovement(int ITEMS_SEPARATION_LINE_HEIGHT , int screenWidth, int bossWidth, int bossHeight, int childSpeed) {
        this.bottomHeight = ITEMS_SEPARATION_LINE_HEIGHT;
        this.wallWidth = screenWidth;
        this.DeltaBossWidth = bossWidth;
        this.DeltaBossHeight = bossHeight;
        this.speed = childSpeed;
    }

    private void moveToTarget(MidBossMob ship, int targetX, int targetY) {
        double dx = targetX - ship.getPositionX();
        double dy = targetY - ship.getPositionY();
        double distance = Math.sqrt(dx*dx + dy*dy);

        double speedMultiplier = (distance > 10) ? 2.0 : 1.0;

        int moveX = (int) (dx * this.speed / distance * speedMultiplier);
        int moveY = (int) (dy * this.speed / distance * speedMultiplier);

        if (Math.abs(moveX) > Math.abs(dx)) moveX = (int) dx;
        if (Math.abs(moveY) > Math.abs(dy)) moveY = (int) dy;

        ship.move(moveX, moveY);

    }

    public void pattern_1_Movement(int bossX, int bossY, List<MidBossMob> Boss_Delta_Childs, int initialChildCount){
        this.frameCount++;
        this.ChildShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.DeltaBossWidth/2;
        int bossCenterY = bossY + this.DeltaBossHeight/2;


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

                if(finalTargetY < 50) {
                    moveToTarget(child, finalTargetX, 50);
                }else {
                    moveToTarget(child, finalTargetX, finalTargetY);
                }
            }
        }
    }

    public void pattern_2_Movement(int bossX, int bossY, List<MidBossMob> Boss_Delta_Childs, int initialChildCount){
        this.frameCount++;
        this.ChildShips = Boss_Delta_Childs;
        int bossCenterX = bossX + this.DeltaBossWidth/2;
        int bossCenterY = bossY + this.DeltaBossHeight/2;

        for(int childIndex = 0; childIndex < this.ChildShips.size(); childIndex++){
            MidBossMob child = this.ChildShips.get(childIndex);
            if(child.getHealPoint() > child.getMaxHp()*0.5){
                double radius = this.ORBIT_RADIUS + (this.ORBIT_RADIUS / initialChildCount);
                double angle = (this.frameCount * this.ORBIT_SPEED) + (childIndex * 2 * Math.PI / initialChildCount);

                int orbitTargetX = (int) (bossCenterX + Math.cos(angle) * radius);
                int orbitTargetY = (int) (bossCenterY + Math.sin(angle) * radius);

                int finalTargetX = orbitTargetX - child.getWidth()/ 2;
                int finalTargetY = orbitTargetY - child.getHeight()/ 2;
                if(finalTargetY < 50) {
                    moveToTarget(child, finalTargetX, 50);
                }else {
                    moveToTarget(child, finalTargetX, finalTargetY);
                }
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

    @Override
    public void updateMovement() {

    }

    @Override
    public void activateSlowdown() {

    }
}
