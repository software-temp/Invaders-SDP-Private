package engine.renderer;

import java.awt.Graphics;
import java.util.Map;

import engine.BackBuffer;
import entity.Entity;
import engine.DrawManager.SpriteType;
import entity.LaserBullet;

/**
 * Handles rendering of all game entities using the shared back buffer.
 * Acts as a sub-view in MVC architecture.
 */
public final class EntityRenderer {

    private final Map<SpriteType, boolean[][]> spriteMap;
    private final BackBuffer backBuffer;

    public EntityRenderer(Map<SpriteType, boolean[][]> spriteMap, BackBuffer backBuffer) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
    }

    /** Draws a single entity on the back buffer. */
    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
        boolean[][] image = spriteMap.get(entity.getSpriteType());
        Graphics g = backBuffer.getGraphics();

        g.setColor(entity.getColor());
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image[i][j]) {
                    g.drawRect(positionX + i * 2, positionY + j * 2, 1, 1);
                }
            }
        }
    }

	public void drawEntity(final Entity entity) {
		if (entity instanceof LaserBullet) {
			LaserBullet laser = (LaserBullet) entity;
			drawLaserRotated(laser, laser.getTargetPosition().x, laser.getTargetPosition().y);
		} else {
			drawEntity(entity, entity.getPositionX(), entity.getPositionY());
		}
	}

	public void drawLaserRotated(Entity entity, int posX, int posY) {
		Graphics g = backBuffer.getGraphics();
		g.setColor(entity.getColor());
		int x1 = posX;
		int y1 = posY;
		int x2 = entity.getPositionX();
		int y2 = entity.getPositionY();

		double dx = x2 - x1;
		double dy = y2 - y1;
		double len = Math.sqrt(dx * dx + dy * dy);
		if (len == 0) {
			return;
		}

		dx /= len;
		dy /= len;

		double big = 2000.0;

		int sx = (int) Math.round(x1 - dx * big);
		int sy = (int) Math.round(y1 - dy * big);
		int ex = (int) Math.round(x1 + dx * big);
		int ey = (int) Math.round(y1 + dy * big);

		g.drawLine(sx, sy, ex, ey);
	}
}
