package entity;

import engine.Cooldown;
import engine.Core;

import java.awt.*;

public class LaserBullet extends Bullet {
	private final Point currentPosition;
	private Point targetPosition;
	private Point velocity;
	private Cooldown chargeCooldown;
	private int chargeCooldownMilli;

	public LaserBullet(Point startPosition, Point targetPosition, int chargeCooldownMilli) {
		super(startPosition.x, startPosition.y, 0, Color.green);
		this.currentPosition=startPosition;
		this.targetPosition=targetPosition;
		setSpeed(targetPosition);
		this.chargeCooldownMilli=chargeCooldownMilli;
		this.chargeCooldown = Core.getCooldown(chargeCooldownMilli);
	}

	@Override
	public void update(){
		this.chargeCooldown.reset();
		if(this.chargeCooldown.checkFinished()){
			currentPosition.x+=velocity.x;
			currentPosition.y+=velocity.y;
		}
	}

	public void setTarget(Point targetPosition){ this.targetPosition = targetPosition; }

	public void setChargeCooldown(int chargeCooldownMilli){
		this.chargeCooldownMilli=chargeCooldownMilli;
		this.chargeCooldown = Core.getCooldown(chargeCooldownMilli);
	}

	private void setSpeed(Point targetPosition){
		this.velocity = new Point((targetPosition.x-positionX)/Core.FPS,(targetPosition.y-positionY)/Core.FPS);
	}
}
