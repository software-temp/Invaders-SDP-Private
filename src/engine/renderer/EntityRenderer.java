package engine.renderer;

import java.awt.*;
import java.util.Map;

import engine.BackBuffer;
import entity.Entity;
import engine.DrawManager.SpriteType;
import entity.LaserBullet;
import screen.HealthBar;

/**
 * Handles rendering of all game entities using the shared back buffer.
 * Acts as a sub-view in MVC architecture.
 */
public final class EntityRenderer {

    private final Map<SpriteType, boolean[][]> spriteMap;
    private final BackBuffer backBuffer;
    private final double scale;

    public EntityRenderer(Map<SpriteType, boolean[][]> spriteMap, BackBuffer backBuffer, double scale) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
        this.scale = scale;
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
    public void drawHealthBar(final HealthBar healthBar){
        int [] position = healthBar.getPosition();
        float ratio_hp = healthBar.getRatio_HP();

        int width = healthBar.getWidth();
        Graphics2D g2 = (Graphics2D) backBuffer.getGraphics();
        g2.setColor(Color.GREEN);
        Stroke oldStroke = g2.getStroke(); // 선 굵기 백업
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(position[0], position[1], position[2], position[3]);
        if ((int)((float)position[0] + (width * ratio_hp)) != position[2]){
            g2.setColor(Color.RED);
            g2.drawLine((int) ((float)position[0] + (width * ratio_hp)), position[1], position[2], position[3]);
        }
        g2.setStroke(oldStroke); // 백업 받은거 원위치
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
