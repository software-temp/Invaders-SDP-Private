package entity.pattern;

import engine.Core;
import entity.GameConstant;
import entity.HasBounds;

import java.awt.*;
import java.util.logging.Logger;

public class DashPattern extends BossPattern {

    protected Logger logger;
    private boolean isDashing = false;
    private boolean isShowingPath = false;
    private HasBounds target;
    private HasBounds boss;
    private double dashDirectionX;
    private double dashDirectionY;
    private long pathShowStartTime;
    private static final long PATH_SHOW_DURATION = 2000; // 2 seconds
    private static final int DASH_SPEED = 10;
    private boolean dashSkillInitialized = false;

    public DashPattern(HasBounds boss, HasBounds target) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.target = target;
        this.boss = boss;
        this.logger = Core.getLogger();

        // Initialize when pattern starts
        if (!dashSkillInitialized) {
            isShowingPath = true;
            pathShowStartTime = System.currentTimeMillis();
            dashSkillInitialized = true;
            logger.info("OMEGA : Dash skill initiated");
        }
    }

    @Override
    public void move() {
        // Continue dashing if already in dash
        if (isDashing) {
            dashToTarget();
            return;
        }

        // Check time if showing path
        if (isShowingPath) {
            long elapsedTime = System.currentTimeMillis() - pathShowStartTime;

            if (elapsedTime >= PATH_SHOW_DURATION) {
                // Calculate dash direction after 2 seconds
                int dx = target.getPositionX() - this.bossPosition.x;
                int dy = target.getPositionY() - this.bossPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Distance check
                if (distance < 1.0) {
                    logger.warning("OMEGA : Player too close, aborting dash");
                    return;
                }

                // Calculate normalized direction
                dashDirectionX = dx / distance;
                dashDirectionY = dy / distance;

                // Start dashing
                isShowingPath = false;
                isDashing = true;
                logger.info("OMEGA : Dashing! Direction=(" + dashDirectionX + ", " + dashDirectionY + ")");
            }
        }
    }

    /**
     * Dash movement logic
     */
    private void dashToTarget() {
        // Move precisely using double direction values
        int newX = boss.getPositionX() + (int)(dashDirectionX * DASH_SPEED);
        int newY = boss.getPositionY() + (int)(dashDirectionY * DASH_SPEED);

        // Check boundaries with boss size
        boolean hitBoundary = false;

        if (newX <= 0) {
            newX = 0;
            hitBoundary = true;
        } else if (newX + boss.getWidth() >= GameConstant.SCREEN_WIDTH) {
            newX = GameConstant.SCREEN_WIDTH - boss.getWidth();
            hitBoundary = true;
        }

        if (newY <= GameConstant.STAT_SEPARATION_LINE_HEIGHT) {
            newY = GameConstant.STAT_SEPARATION_LINE_HEIGHT;
            hitBoundary = true;
        } else if (newY + boss.getHeight() >= GameConstant.ITEMS_SEPARATION_LINE_HEIGHT) {
            newY = GameConstant.ITEMS_SEPARATION_LINE_HEIGHT - boss.getHeight();
            hitBoundary = true;
        }

        // Update boss position
        this.bossPosition.x = newX;
        this.bossPosition.y = newY;

        // End dash when hitting wall
        if (hitBoundary) {
            logger.info("OMEGA : Dash completed, hit the wall");
            isDashing = false;
        }
    }

    @Override
    public void attack() {
        // DashPattern is a movement pattern, so no attack logic
    }

    @Override
    public void setTarget(HasBounds target) {
        this.target = target;
    }

    // Getter methods
    public boolean isShowingPath() {
        return isShowingPath;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public boolean isDashCompleted() {
        return !isDashing && !isShowingPath && dashSkillInitialized;
    }

    /**
     * Calculate dash end point (for visualization)
     */
    public int[] getDashEndPoint(int bossWidth, int bossHeight) {
        if (isShowingPath) {
            // Return player position when showing path
            return new int[]{
                    target.getPositionX() + target.getWidth() / 2,
                    target.getPositionY() + target.getHeight() / 2
            };
        }

        // Calculate wall position when dashing
        int endX = bossPosition.x;
        int endY = bossPosition.y;

        if (dashDirectionX > 0) {
            endX = GameConstant.SCREEN_WIDTH - bossWidth;
        } else if (dashDirectionX < 0) {
            endX = 0;
        }

        if (dashDirectionY > 0) {
            endY = GameConstant.ITEMS_SEPARATION_LINE_HEIGHT - bossHeight;
        } else if (dashDirectionY < 0) {
            endY = GameConstant.ITEMS_SEPARATION_LINE_HEIGHT - bossHeight;
        }

        return new int[]{endX + bossWidth / 2, endY + bossHeight / 2};
    }
}