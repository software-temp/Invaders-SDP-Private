package entity.pattern;

import engine.Cooldown;
import engine.Core;
import entity.Bullet;
import entity.LaserBullet;

import java.awt.*;

public class TimeGapAttackPattern extends BossPattern {

	private final int shootCooldownMilli = 600;
	private final int chargeColldownMilli = 1000;
	private Cooldown shootCooldown;
	private Point targetPosition;
	private Bullet bullet;

	private final int screenWidth;
	private final int screenHeight;

	public TimeGapAttackPattern(Point bossPosition, Point targetPosition, int screenWidth, int screenHeight) {
		super(bossPosition);
		this.targetPosition=targetPosition;
		this.shootCooldown = Core.getCooldown(shootCooldownMilli);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Override
	public void attack() {
		if(this.shootCooldown.checkFinished()){
			this.shootCooldown.reset();
			int randomX = (int) (Math.random() * screenWidth);
			int randomY = (int) (Math.random() * screenHeight);
			Point initBulletPosition = new Point(randomX,randomY);
			bullet = new LaserBullet(initBulletPosition, targetPosition, chargeColldownMilli);
			super.bullets.add(bullet);
		}
	}

	@Override
	public void move() {
		// move to the center of the screen.
	}
}
