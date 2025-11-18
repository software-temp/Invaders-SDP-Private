package entity;

import engine.DrawManager;

import java.awt.*;


public class BossBullet extends Bullet implements Collidable {
	/**
	 * amount of horizontal change
	 */
	private final int dx;
	/**
	 * amount of vertical change
	 */
	private final int dy;
	/**
	 * bossBullets carry bullets that the boss will shoot
	 * Constructor, establishes boss bullets.
	 *
	 * @param x      current x-coordinate
	 * @param y      current y-coordinate
	 * @param dx     amount of horizontal change
	 * @param dy     amount of vertical change
	 * @param width  bullet's width
	 * @param height bullet's height
	 * @param color  bullet's color
	 */
	public BossBullet(int x, int y, int dx, int dy, int width, int height, Color color) {
		super(x, y, 0, color);
		this.dx = dx;
		this.dy = dy;
		this.width = width;
		this.height = height;
		this.spriteType = DrawManager.SpriteType.FinalBossBullet; // boss's bullet image = enemyBullet
	}

	/**
	 * move a bullet
	 */
	public void update() {
		this.positionX += this.dx;
		this.positionY += this.dy;
	}

	/**
	 * does the bullet go off the screen
	 */
	public boolean isOffScreen(int screenWidth, int screenHeight) {
		return positionX < 0 || positionX > screenWidth ||
				positionY < 0 || positionY > screenHeight;
	}

	/**
	 * Handles collision behavior for boss bullets.
	 * Boss bullets damage the player when they collide.
	 */
	@Override
	public void onCollision(Collidable other, GameModel game) {
		if (other instanceof Ship) {
			game.handleBossBulletHitPlayer(this, (Ship) other);
		}
	}
}
