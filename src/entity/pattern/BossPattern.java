package entity.pattern;

import engine.Cooldown;
import entity.Bullet;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class BossPattern implements IBossPattern {
	protected Point bossPosition;
	protected Point velocity;
	protected Point acceleration;
	protected Cooldown shootCooldown;
	protected Set<Bullet> bullets;

	public BossPattern(Point position) {
		this.bossPosition = position;
		this.bullets = new HashSet<Bullet>();
	}

	public Cooldown getShootCoolDown(Cooldown cooldown) { return cooldown; }
	public void setCooldown(Cooldown cooldown) { this.shootCooldown=cooldown; }
	public Point getBossPosition() { return this.bossPosition; }
	public Set<Bullet> getBullets(){
		if(!this.bullets.isEmpty()){
			return this.bullets;
		}
		else{
			return java.util.Collections.emptySet();
		}
	}
}
