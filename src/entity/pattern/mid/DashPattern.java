package entity.pattern.mid;

import engine.Core;
import engine.DrawManager;
import entity.HasBounds;
import entity.Ship;
import entity.pattern.BossPattern;

import java.awt.*;
import java.util.logging.Logger;

public class DashPattern extends BossPattern {

    private final int screenWidth;
    private final int lowerBoundary;
    private final int upperBoundary;
    protected Logger logger;

    // Dash 관련 필드
    private boolean isDashing = false;
    private boolean isShowingPath = false;
    private HasBounds target;
    private double dashDirectionX;
    private double dashDirectionY;
    private long pathShowStartTime;
    private static final long PATH_SHOW_DURATION = 2000; // 2초
    private static final int DASH_SPEED = 10;
    private boolean dashSkillInitialized = false;

    public DashPattern(HasBounds boss, HasBounds target, int screenWidth, int lowerBoundary, int upperBoundary) {
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.target = target;
        this.screenWidth = screenWidth;
        this.lowerBoundary = lowerBoundary;
        this.upperBoundary = upperBoundary;
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
        // double 방향값으로 정밀하게 이동
        this.bossPosition.x += (int)(dashDirectionX * DASH_SPEED);
        this.bossPosition.y += (int)(dashDirectionY * DASH_SPEED);

        // 벽에 닿으면 돌진 종료 및 패턴 완료 플래그 설정
        if (this.bossPosition.x <= 0 || this.bossPosition.x >= screenWidth ||
                this.bossPosition.y <= upperBoundary || this.bossPosition.y >= lowerBoundary) {
            logger.info("OMEGA : Dash completed, hit the wall");
            isDashing = false;
            // 이 시점에서 OmegaBoss가 패턴을 변경할 수 있도록 플래그 설정
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
            endX = screenWidth - bossWidth;
        } else if (dashDirectionX < 0) {
            endX = 0;
        }

        if (dashDirectionY > 0) {
            endY = lowerBoundary - bossHeight;
        } else if (dashDirectionY < 0) {
            endY = upperBoundary - bossHeight;
        }

        return new int[]{endX + bossWidth / 2, endY + bossHeight / 2};
    }
}