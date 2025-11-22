package entity.pattern;

import entity.Entity;
import entity.GameConstant;
import entity.HasBounds;

import java.awt.*;

/**
 * Diagonal movement pattern for middle boss
 * Combines horizontal and vertical movement with boundary bouncing
 */
public class DiagonalPattern extends BossPattern {

    private HasBounds bossBound;
    private final int horizontalSpeed;
    private final int verticalSpeed;
    private final Color patternColor;
    private boolean isRight;
    private boolean isDown;

    /**
     * Constructor for diagonal pattern
     * @param bossBound The boss entity
     * @param horizontalSpeed Horizontal movement speed
     * @param verticalSpeed Vertical movement speed
     * @param patternColor Color for this pattern
     */
    public DiagonalPattern(HasBounds bossBound, int horizontalSpeed, int verticalSpeed, Color patternColor) {
        super(new Point(bossBound.getPositionX(), bossBound.getPositionY()));
        this.bossBound = bossBound;
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.patternColor = patternColor;
        this.isRight = true;
        this.isDown = true;

        // Update boss appearance for this pattern

    }

    @Override
    public void move() {
        int dx = isRight ? horizontalSpeed : -horizontalSpeed;
        int dy = isDown ? verticalSpeed : -verticalSpeed;

        int newX = bossBound.getPositionX() + dx;
        int newY = bossBound.getPositionY() + dy;

        // Check horizontal boundaries
        if (newX <= 0) {
            newX = 0;
            isRight = true;
        } else if (newX + bossBound.getWidth() >= GameConstant.SCREEN_WIDTH) {
            newX = GameConstant.SCREEN_WIDTH - bossBound.getWidth();
            isRight = false;
        }

        // Check vertical boundaries
        if (newY <= GameConstant.STAT_SEPARATION_LINE_HEIGHT) {
            newY = GameConstant.STAT_SEPARATION_LINE_HEIGHT;
            isDown = true;
        } else if (newY + bossBound.getHeight() >= GameConstant.ITEMS_SEPARATION_LINE_HEIGHT) {
            newY = GameConstant.ITEMS_SEPARATION_LINE_HEIGHT - bossBound.getHeight();
            isDown = false;
        }
        this.bossPosition.x = newX;
        this.bossPosition.y = newY;
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