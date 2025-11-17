package entity;

import engine.DrawManager;

import java.awt.*;


public class BossBullet extends Bullet {
    /** amount of horizontal change*/
    private int dx;
    /** amount of vertical change*/
    private int dy;
    /** bossBullets carry bullets that the boss will shoot */
    /**
     * Constructor, establishes boss bullets.
     *
     * @param x
     *            current x-coordinate
     * @param y
     *            current y-coordinate
     * @param dx
     *            amount of horizontal change
     * @param dy
     *            amount of vertical change
     * @param width
     *            bullet's width
     * @param height
     *            bullet's height
     * @param color
     *            bullet's color
     */
    public BossBullet(int x, int y, int dx, int dy, int width, int height, Color color) {
        super(x, y, 0, color);
		super.width = width;
		super.height = height;
        this.dx = dx;
        this.dy = dy;
        this.spriteType = DrawManager.SpriteType.FinalBossBullet; // boss's bullet image = enemyBullet
    }
    /**
     * move a bullet
     */
	@Override
    public void update() {
        this.positionX += this.dx;
        this.positionY += this.dy;
    }
}
