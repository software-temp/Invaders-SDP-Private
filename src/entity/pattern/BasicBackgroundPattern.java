package entity.pattern;

import engine.Cooldown;
import engine.Core;
import entity.BossBullet;

import java.awt.*;

public class BasicBackgroundPattern extends BossPattern {

	protected Cooldown shootCooldown;

	protected final int screenWidth;

	public BasicBackgroundPattern(int screenWidth) {
		super(new Point(0,0));
		this.screenWidth = screenWidth;
		int shootCooldownMilli = 400;
		this.shootCooldown = Core.getCooldown(shootCooldownMilli);
	}

	@Override
	public void attack() {
		if (this.shootCooldown.checkFinished()) {
			this.shootCooldown.reset();
			int randomX = (int) (Math.random() * screenWidth);
			BossBullet bullet = new BossBullet(randomX, 1, 0, 2,6,10, Color.yellow);
			bullets.add(bullet);
		}
	}

	@Override
	public void move() {

	}
}
