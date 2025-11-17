package entity.pattern.mid;

import entity.Entity;
import entity.HasBounds;
import entity.pattern.BossPattern;

import java.awt.*;

/**
 * Horizontal movement pattern for middle boss
 * Simple side-to-side movement across the screen
 */
public class HorizontalPattern extends BossPattern {

    private Entity boss;
    private final int widthBoundary;
    private final int speed;
    private boolean isRight;

    /**
     * Constructor for horizontal pattern
     * @param boss The boss entity
     * @param widthBoundary Right boundary limit
     * @param speed Movement speed
     */
    public HorizontalPattern(Entity boss, int widthBoundary, int speed) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.boss = boss;
        this.widthBoundary = widthBoundary;
        this.speed = speed;
        this.isRight = true;
    }

    @Override
    public void move() {
        int dx = isRight ? speed : -speed;

        int newX = boss.getPositionX() + dx;

        // Check boundaries and reverse direction
        if (newX <= 0) {
            newX = 0;
            isRight = true;
        } else if (newX + boss.getWidth() >= widthBoundary) {
            newX = widthBoundary - boss.getWidth();
            isRight = false;
        }

        boss.setPositionX(newX);
    }

    @Override
    public void attack() {
        // No attack in this pattern
    }

    @Override
    public Point getBossPosition() {
        return new Point(boss.getPositionX(), boss.getPositionY());
    }

    @Override
    public void setTarget(HasBounds target) {
        // No target needed for this pattern
    }
}