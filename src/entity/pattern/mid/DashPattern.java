package entity.pattern.mid;

import engine.Core;
import engine.DrawManager;
import entity.GameConstant;
import entity.HasBounds;
import entity.Ship;
import entity.pattern.BossPattern;

import java.awt.*;
import java.util.logging.Logger;

public class DashPattern extends BossPattern {

    protected Logger logger;

    // Dash 관련 필드
    private boolean isDashing = false;
    private boolean isShowingPath = false;
    private HasBounds target;
    private HasBounds boss;
    private double dashDirectionX;
    private double dashDirectionY;
    private long pathShowStartTime;
    private static final long PATH_SHOW_DURATION = 2000; // 2초
    private static final int DASH_SPEED = 10;
    private boolean dashSkillInitialized = false;

    public DashPattern(HasBounds boss, HasBounds target) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.target = target;
        this.boss = boss;
        this.logger = Core.getLogger();

        // 패턴 시작 시 초기화
        if (!dashSkillInitialized) {
            isShowingPath = true;
            pathShowStartTime = System.currentTimeMillis();
            dashSkillInitialized = true;
            logger.info("OMEGA : Dash skill initiated");
        }
    }

    @Override
    public void move() {
        // 이미 돌진 중이면 계속 돌진
        if (isDashing) {
            dashToTarget();
            return;
        }

        // 경로 표시 중이면 시간 체크
        if (isShowingPath) {
            long elapsedTime = System.currentTimeMillis() - pathShowStartTime;

            if (elapsedTime >= PATH_SHOW_DURATION) {
                // 2초 지나면 돌진 방향 계산
                int dx = target.getPositionX() - this.bossPosition.x;
                int dy = target.getPositionY() - this.bossPosition.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // 거리 체크
                if (distance < 1.0) {
                    logger.warning("OMEGA : Player too close, aborting dash");
                    return;
                }

                // 정규화된 방향 계산
                dashDirectionX = dx / distance;
                dashDirectionY = dy / distance;

                // 돌진 시작
                isShowingPath = false;
                isDashing = true;
                logger.info("OMEGA : Dashing! Direction=(" + dashDirectionX + ", " + dashDirectionY + ")");
            }
        }
    }

    /**
     * Dash 이동 로직
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
        // DashPattern은 이동 패턴이므로 공격 로직 없음
    }

    @Override
    public void setTarget(HasBounds target) {
        this.target = target;
    }

    // Getter 메서드들
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
     * Dash 종료 지점 계산 (시각화용)
     */
    public int[] getDashEndPoint(int bossWidth, int bossHeight) {
        if (isShowingPath) {
            // 경로 표시 중일 때는 플레이어 위치 반환
            return new int[]{
                    target.getPositionX() + target.getWidth() / 2,
                    target.getPositionY() + target.getHeight() / 2
            };
        }

        // 돌진 중일 때는 벽 위치 계산
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