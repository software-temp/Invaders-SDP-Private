package entity.pattern;

import engine.Cooldown;
import entity.BossBullet;
import entity.Bullet;
import entity.HasBounds;

import java.awt.*;
import java.util.Set;

public class PinnedBossPattern extends BossPattern implements HasBounds{

	protected Cooldown shootCooldown;
	protected HasBounds boss;

	/** for move pattern */
	protected int zigDirection = 1;
	/** for move pattern */
	protected boolean goingDown = true;

	protected final int screenWidth;
	protected final int screenHeight;

	protected BossPattern backGroundPattern;

	public PinnedBossPattern(HasBounds boss, int screenWidth, int screenHeight) {
		super(new Point(boss.getPositionX(), boss.getPositionY()));
		this.boss = boss;
		this.screenWidth=screenWidth;
		this.screenHeight=screenHeight;
		int shootCooldownMilli = 5000;
		this.shootCooldown= new Cooldown(shootCooldownMilli);
		backGroundPattern = new BasicBackgroundPattern(screenWidth);
	}

	@Override
	public void attack() {
		backGroundPattern.attack();
		if(this.shootCooldown.checkFinished()){
			this.shootCooldown.reset();
			int[] arr = {0,1,-1,2,-2};
			for (int i : arr){
				BossBullet bullet = new BossBullet(this.boss.getPositionX() + this.boss.getWidth()/ 2, this.boss.getPositionY() + boss.getHeight(),i,4,6,10,Color.yellow);
				bullets.add(bullet);
			}
		}
	}

	@Override
	public void move() {

	}

	@Override
	public Set<Bullet> getBullets(){
		this.bullets.addAll(backGroundPattern.getBullets());
		return bullets;
	}

	@Override
	public int getPositionX() {
		return 0;
	}

	@Override
	public int getPositionY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}
}
