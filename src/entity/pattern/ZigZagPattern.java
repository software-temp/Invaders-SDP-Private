package entity.pattern;

import engine.Cooldown;
import entity.HasBounds;

public class ZigZagPattern extends PinnedBossPattern {

	public ZigZagPattern(HasBounds boss, int screenWidth, int screenHeight) {
		super(boss, screenWidth, screenHeight);
		int shootCooldownMilli = 2000;
		shootCooldown = new Cooldown(shootCooldownMilli);
	}

	@Override
	public void move(){
		moveZigzag(4,3);
	}

	protected void moveZigzag(int zigSpeed, int vertSpeed){
		super.bossPosition.x += (this.zigDirection * zigSpeed);
		if(this.bossPosition.x <= 0 || this.bossPosition.x >= screenWidth-boss.getWidth()){
			this.zigDirection*=-1;
		}

		if(goingDown){
			this.bossPosition.y += vertSpeed;
			if (this.bossPosition.y >= screenHeight/2 - boss.getHeight()){ goingDown = false; }
		}
		else{
			this.bossPosition.y -= vertSpeed;
			if(this.bossPosition.y <= 0){ goingDown = true; }
		}
	}
}
