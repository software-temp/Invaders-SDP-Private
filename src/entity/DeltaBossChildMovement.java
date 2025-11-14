package entity;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import entity.EnemyShip;

public class DeltaBossChildMovement implements IMovementStrategy{

    private int weight;
    private int bottomHeight;
    private List<EnemyShip> ChildShips;
    private int speed;

    public DeltaBossChildMovement(List<EnemyShip> Boss_Delta_Childs,int ITEMS_SEPARATION_LINE_HEIGHT ,int screenWeight,int childSpeed) {
        this.ChildShips = Boss_Delta_Childs;
        this.bottomHeight = ITEMS_SEPARATION_LINE_HEIGHT;
        this.weight = screenWeight;
        this.speed = childSpeed;
    }
    @Override
    public void updateMovement() {
        if(!this.ChildShips.isEmpty()) {
            for(EnemyShip ship : this.ChildShips) {
                if(ship.getDirection()== null || ship.getDirection() == EnemyShip.Direction.RIGHT){
                    boolean check_right_wall =ship.getPositionX() + ship.getWidth()+ship.getXSpeed() > this.weight;
                    if(check_right_wall){ ship.setDirection(EnemyShip.Direction.LEFT); }
                    else{ ship.move(this.speed,0); }
                }else {
                    boolean check_left_wall =ship.getPositionX() < 0;
                    if(check_left_wall) { ship.setDirection(EnemyShip.Direction.RIGHT); }
                    else{ ship.move(-this.speed,0); }
                }
            }
        }
    }

    @Override
    public void activateSlowdown() {

    }
}
