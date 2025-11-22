package engine.renderer;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.*;
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
    private final double scale;
    private static final Color BLACK_HOLE_COLOR = new Color(200, 0, 255);
    public EntityRenderer(Map<SpriteType, boolean[][]> spriteMap, BackBuffer backBuffer, double scale) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
        this.scale = scale;
    }
	public void drawEntity(SpriteType sprite, Color color, int x, int y) {
		boolean[][] image = spriteMap.get(sprite);
		Graphics g = backBuffer.getGraphics();
		g.setColor(color);

		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				if (image[i][j]) {
					int pixelSize = (int) Math.max(1, 2 * scale);
					int scaledX = x + (int)(i * pixelSize);
					int scaledY = y + (int)(j * pixelSize);
					g.fillRect(scaledX, scaledY, pixelSize, pixelSize);
				}
			}
		}
	}

    /** Draws a single entity on the back buffer. */
    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
        boolean[][] image = spriteMap.get(entity.getSpriteType());
        Graphics g = backBuffer.getGraphics();
        g.setColor(entity.getColor());

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image[i][j]) {
                    int pixelSize = (int) Math.max(1, 2 * scale);
                    int scaledX = positionX + (int)(i * pixelSize);
                    int scaledY = positionY + (int)(j * pixelSize);
                    g.fillRect(scaledX, scaledY, pixelSize, pixelSize);
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

    /** Draw circle for pull_attack pattern */
    public void drawBlackHole(final int cx, final int cy, final int radius, final boolean isBlackHoleActive){
        if(isBlackHoleActive){
            int x = cx - radius;
            int y = cy - radius;

            Graphics g = backBuffer.getGraphics();
            g.setColor(BLACK_HOLE_COLOR);
            g.drawOval(x, y, radius * 2, radius * 2);
        }
    }

}
