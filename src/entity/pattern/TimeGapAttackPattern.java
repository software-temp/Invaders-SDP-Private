package entity.pattern;

import engine.Cooldown;
import engine.Core;
import entity.Bullet;
import entity.HasBounds;
import entity.LaserBullet;

import java.awt.*;

public class TimeGapAttackPattern extends BossPattern {

	private final int shootCooldownMilli = 400;
	private final int chargeCooldownMilli = 150;
	private Cooldown shootCooldown;
	private HasBounds target;
	private Point targetPosition;
	private Bullet bullet;
	private boolean isUpdated=false;

	private final int screenWidth;
	private final int screenHeight;

	public TimeGapAttackPattern(Point bossPosition, HasBounds target, int screenWidth, int screenHeight) {
		super(bossPosition);
		this.target = target;
		this.shootCooldown = Core.getCooldown(shootCooldownMilli);
		this.shootCooldown.reset();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	@Override
	public void attack() {
		if(!isUpdated){
			this.targetPosition = new Point(target.getPositionX(),target.getPositionY());
			isUpdated=true;
		}
		if(this.shootCooldown.checkFinished()){
			this.shootCooldown.reset();
			this.isUpdated=false;
			int randomX = (int) (Math.random() * screenWidth);
			int randomY = (int) (Math.random() * screenHeight);
			Point initBulletPosition = new Point(randomX,randomY);
			this.bullet = new LaserBullet(initBulletPosition, targetPosition, chargeCooldownMilli);
			this.bullets.add(bullet);
		}
	}

	@Override
	public void move() {
		// move to the center of the screen.
	}
}
