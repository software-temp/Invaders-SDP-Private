package entity;

import engine.Cooldown;
import engine.Core;

import java.awt.*;

public class LaserBullet extends Bullet {
	private final Point currentPosition;
	private Point targetPosition;
	private Point velocity;
	private double theta;
	private Cooldown chargeCooldown;
	private int chargeCooldownMilli;

	private boolean isChargeCooldownFinished=false;
	double speedPerSecond = 1200.0;

	public LaserBullet(Point startPosition, Point targetPosition, int chargeCooldownMilli) {
		super(startPosition.x, startPosition.y, 0, Color.green);
//		super.height=40;
//		super.width=30;
		this.currentPosition=startPosition;
		this.targetPosition=targetPosition;
		this.chargeCooldownMilli=chargeCooldownMilli;
		this.chargeCooldown = Core.getCooldown(chargeCooldownMilli);
		this.chargeCooldown.reset();
	}

	@Override
	public void update(){
		if(this.chargeCooldown.checkFinished()){
			setSpeed();
			setRotation();
			isChargeCooldownFinished=true;
		}
		if(isChargeCooldownFinished){
			this.positionX+=velocity.x;
			this.positionY+=velocity.y;
		}
	}

	public void setTarget(Point targetPosition){ this.targetPosition = targetPosition; }

	public void setChargeCooldown(int chargeCooldownMilli){
		this.chargeCooldownMilli=chargeCooldownMilli;
		this.chargeCooldown = Core.getCooldown(chargeCooldownMilli);
	}

	private void setSpeed() {
		double dx = targetPosition.x - this.currentPosition.x;
		double dy = targetPosition.y - this.currentPosition.y;
		double dist = Math.sqrt(dx * dx + dy * dy);

		if(dist==0) velocity = new Point(0, 0);
		else {
			double speedPerFrame = speedPerSecond / Core.FPS;

			double nx = dx / dist;
			double ny = dy / dist;

			this.velocity = new Point((int)(nx * speedPerFrame), (int)(ny * speedPerFrame));
		}
	}

	private void setRotation() {
		double dx = targetPosition.x - this.currentPosition.x;
		double dy = targetPosition.y - this.currentPosition.y;
		this.theta = Math.atan2(dy, dx);
	}
}
