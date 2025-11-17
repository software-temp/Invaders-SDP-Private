package entity.pattern.mid;

import engine.DrawManager;
import entity.Entity;
import entity.HasBounds;
import entity.pattern.BossPattern;

import java.awt.*;

/**
 * Diagonal movement pattern for middle boss
 * Combines horizontal and vertical movement with boundary bouncing
 */
public class DiagonalPattern extends BossPattern {

    private Entity boss;
    private final int widthBoundary;
    private final int lowerBoundary;
    private final int upperBoundary;
    private final int horizontalSpeed;
    private final int verticalSpeed;
    private final Color patternColor;
    private boolean isRight;
    private boolean isDown;

    /**
     * Constructor for diagonal pattern
     * @param boss The boss entity
     * @param widthBoundary Right boundary limit
     * @param lowerBoundary Lower boundary limit
     * @param upperBoundary Upper boundary limit (initial Y position)
     * @param horizontalSpeed Horizontal movement speed
     * @param verticalSpeed Vertical movement speed
     * @param patternColor Color for this pattern
     */
    public DiagonalPattern(Entity boss, int widthBoundary, int lowerBoundary,
                           int upperBoundary, int horizontalSpeed, int verticalSpeed,
                           Color patternColor) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.boss = boss;
        this.widthBoundary = widthBoundary;
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.patternColor = patternColor;
        this.isRight = true;
        this.isDown = true;

        // Update boss appearance for this pattern
        boss.setColor(patternColor);
//        boss.(DrawManager.SpriteType.OmegaBoss2);
    }

    @Override
    public void move() {
        int dx = isRight ? horizontalSpeed : -horizontalSpeed;
        int dy = isDown ? verticalSpeed : -verticalSpeed;

        int newX = boss.getPositionX() + dx;
        int newY = boss.getPositionY() + dy;

        // Check horizontal boundaries
        if (newX <= 0) {
            newX = 0;
            isRight = true;
        } else if (newX + boss.getWidth() >= widthBoundary) {
            newX = widthBoundary - boss.getWidth();
            isRight = false;
        }

        // Check vertical boundaries
        if (newY <= upperBoundary) {
            newY = upperBoundary;
            isDown = true;
        } else if (newY + boss.getHeight() >= lowerBoundary) {
            newY = lowerBoundary - boss.getHeight();
            isDown = false;
        }

        boss.setPositionX(newX);
        boss.setPositionY(newY);
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