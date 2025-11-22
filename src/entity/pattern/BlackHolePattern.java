package entity.pattern;

import engine.Cooldown;
import entity.HasBounds;
import entity.Ship;

import java.awt.*;
import java.util.List;

public class BlackHolePattern extends BossPattern{

    private HasBounds boss;
    private List<Ship> ships;

    private int centerX;
    private int centerY;
    private int radius;
    private final double pullConstant;

    private Cooldown durationBlackHole;

    public BlackHolePattern(HasBounds boss, List<Ship> ships, final int centerX, final int centerY, final int radius, final double pullConstant, int duration) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.boss = boss;
        this.ships = ships;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.pullConstant = pullConstant;
        this.durationBlackHole = new Cooldown(duration);
        this.durationBlackHole.reset();
    }

    @Override
    public void attack(){
        if(durationBlackHole.checkFinished()) return;

        for(Ship ship : ships){
            double shipX = ship.getPositionX();
            double shipY = ship.getPositionY();

            double dx = centerX - shipX;
            double dy = centerY - shipY;

            double dist = Math.sqrt(dx * dx + dy * dy);
            if(dist <= radius && dist > 1){
                double force = (radius - dist) * pullConstant;

                double ux = dx/dist;
                double uy = dy/dist;


                ship.setPositionX(ship.getPositionX()+(int)(ux*force));
                ship.setPositionY(ship.getPositionY()+(int)(uy*force));
            }
        }
    }

    public boolean isFinished() {
        return durationBlackHole.checkFinished();
    }

    @Override
    public void move(){

    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isActive() {
        return !durationBlackHole.checkFinished();
    }

}
