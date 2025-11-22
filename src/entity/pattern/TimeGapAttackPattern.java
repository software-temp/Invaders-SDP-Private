package entity.pattern;

import engine.Cooldown;
import entity.Bullet;
import entity.HasBounds;
import entity.LaserBullet;
import entity.Ship;

import java.util.List;
import java.awt.*;

public class TimeGapAttackPattern extends BossPattern {

	private final int shootCooldownMilli = 400;
	private final int chargeCooldownMilli = 200;
	private final int remainCooldownMilli = 1000;
	private final int movingSmoothRatio = 10;
	private Cooldown shootCooldown;
	private HasBounds boss;
	private HasBounds target;
	private Point targetPosition;
	private Bullet bullet;
	private boolean isUpdated=false;

    private List<Ship> ships;

	private final int screenWidth;
	private final int screenHeight;

	public TimeGapAttackPattern(HasBounds boss, List<Ship> ships, int screenWidth, int screenHeight) {
		super(new Point(boss.getPositionX(), boss.getPositionY()));
		this.boss = boss;
        this.ships = ships;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Override
	public void attack() {
		if(this.shootCooldown==null){
			this.shootCooldown = new Cooldown(shootCooldownMilli);
			shootCooldown.reset();
		}
		if(!isUpdated){
            this.target = chooseRandomAliveShip();
            if (this.target == null) return;
            this.targetPosition = new Point(target.getPositionX() + target.getWidth() / 2, target.getPositionY() + target.getHeight() / 2);
            isUpdated=true;
		}
		if(this.shootCooldown.checkFinished()){
			this.shootCooldown.reset();
			this.isUpdated=false;
			int randomX = (int) (Math.random() * screenWidth);
			int randomY = (int) (Math.random() * screenHeight);
			Point initBulletPosition = new Point(randomX,randomY);
			this.bullet = new LaserBullet(initBulletPosition, targetPosition, chargeCooldownMilli, remainCooldownMilli);
			this.bullets.add(bullet);
		}
	}

	@Override
	public void move() {
		int targetCenterX = screenWidth  / 2;
		int targetCenterY = screenHeight / 4;

		int targetX = targetCenterX - boss.getWidth()  / 2;
		int targetY = targetCenterY - boss.getHeight() / 2;

		int dx = targetX - bossPosition.x;
		int dy = targetY - bossPosition.y;

		bossPosition.x += dx / movingSmoothRatio;
		bossPosition.y += dy / movingSmoothRatio;

		if (Math.abs(targetX - bossPosition.x) <= 1) bossPosition.x = targetX;
		if (Math.abs(targetY - bossPosition.y) <= 1) bossPosition.y = targetY;
	}

    /** random choose from ships */
    private Ship chooseRandomAliveShip() {
        List<Ship> aliveShips = new java.util.ArrayList<>();

        for (Ship s : ships) {
            if (s != null && !s.isDestroyed()) {
                aliveShips.add(s);
            }
        }

        if (aliveShips.isEmpty()) return null;

        int idx = (int)(Math.random() * aliveShips.size());
        return aliveShips.get(idx);
    }


    @Override
	public void setTarget(HasBounds target){
		this.target = target;
	}
}
