package entity.pattern;

import engine.Core;
import entity.GameConstant;
import entity.HasBounds;

import java.awt.*;

/**
 * Horizontal movement pattern for middle boss
 * Simple side-to-side movement across the screen
 */
public class HorizontalPattern extends BossPattern {

    private HasBounds bossBound;
    private final int speed;
    private boolean isRight;

    /**
     * Constructor for horizontal pattern
     * @param bossBound The boss entity
     * @param speed Movement speed
     */
    public HorizontalPattern(HasBounds bossBound, int speed) {
        super(new Point(bossBound.getPositionX(), bossBound.getPositionY()));
        this.bossBound = bossBound;
        this.speed = speed;
        this.isRight = true;
    }

    @Override
    public void move() {
        int dx = isRight ? speed : -speed;

        int newX = bossBound.getPositionX() + dx;

        // Check boundaries and reverse direction
        if (newX <= 0) {
            newX = 0;
            isRight = true;
        } else if (newX + bossBound.getWidth() >= GameConstant.SCREEN_WIDTH) {
            newX = GameConstant.SCREEN_WIDTH - bossBound.getWidth();
            isRight = false;
        }
        this.bossPosition.x = newX;
    }

    @Override
    public void attack() {
        // No attack in this pattern
    }

    @Override
    public Point getBossPosition() {
        return new Point(this.bossPosition.x, this.bossPosition.y);
    }

    @Override
    public void setTarget(HasBounds target) {
        // No target needed for this pattern
    }
}