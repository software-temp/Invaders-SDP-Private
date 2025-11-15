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
	private Cooldown remainCooldown;
	private final int chargeCooldownMilli;
	private final int remainCooldownMilli;
	private boolean needToRemove=false;

	double speedPerSecond = 1200.0;

	public LaserBullet(Point startPosition, Point targetPosition, int chargeCooldownMilli, int remainCooldownMilli) {
		super(startPosition.x, startPosition.y, 0, Color.green);
		this.currentPosition=startPosition;
		this.targetPosition=targetPosition;
		this.chargeCooldownMilli=chargeCooldownMilli;
		this.remainCooldownMilli=remainCooldownMilli;
	}

	@Override
	public void update(){
		if(this.chargeCooldown==null){
			this.chargeCooldown = Core.getCooldown(chargeCooldownMilli);
			chargeCooldown.reset();
		}
		if(this.chargeCooldown.checkFinished()){
			setRotation();
			color = Color.red;
			if(this.remainCooldown==null){
				this.remainCooldown = Core.getCooldown(remainCooldownMilli);
				remainCooldown.reset();
			}
		}
		if(this.remainCooldown!=null && this.remainCooldown.checkFinished()){
			needToRemove=true;
		}
	}

	private void setRotation() {
		double dx = targetPosition.x - this.currentPosition.x;
		double dy = targetPosition.y - this.currentPosition.y;
		this.theta = Math.atan2(dy, dx);
	}
	public Point getTargetPosition() {
		return targetPosition;
	}
	public boolean needToRemove() {
		return needToRemove;
	}
}
