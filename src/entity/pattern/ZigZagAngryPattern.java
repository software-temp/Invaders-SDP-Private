package entity.pattern;

import engine.Cooldown;
import entity.BossBullet;
import entity.HasBounds;

import java.awt.*;

public class ZigZagAngryPattern extends ZigZagPattern {

	public ZigZagAngryPattern(HasBounds boss, int screenWidth, int screenHeight) {
		super(boss, screenWidth, screenHeight);
		int shootCooldownMilli = 300;
		shootCooldown = new Cooldown(shootCooldownMilli);
	}

	@Override
	public void attack(){
		if (this.shootCooldown.checkFinished()) {
			this.shootCooldown.reset();
//            if (!(this.getPositionX() == 0 || this.getPositionX() == 400)){
			BossBullet bullet1 = new BossBullet(boss.getPositionX() + boss.getWidth() / 2 - 3 + 70, boss.getPositionY(), 0, 5,6,10, Color.blue);
			BossBullet bullet2 = new BossBullet(boss.getPositionX() + boss.getWidth() / 2 - 3 - 70, boss.getPositionY(), 0, 5,6,10,Color.blue);
			bullets.add(bullet1);
			bullets.add(bullet2);
//            }
		}
	}

	@Override
	public void move(){
		moveZigzag(2,1);
	}
}
