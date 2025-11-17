package engine.renderer;

import java.awt.*;
import java.util.Map;

import engine.BackBuffer;
import engine.Core;
import entity.Entity;
import engine.DrawManager.SpriteType;
import entity.LaserBullet;
import entity.OmegaBoss;
import entity.pattern.BossPattern;

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

	public void drawEntity(final Entity entity) {
		if (entity instanceof LaserBullet) {
			LaserBullet laser = (LaserBullet) entity;
			drawLaserRotated(laser, laser.getTargetPosition().x, laser.getTargetPosition().y);
		}
		else if (entity instanceof OmegaBoss) {
			OmegaBoss omegaBoss = (OmegaBoss) entity;
//			BossPattern dasPattern = omegaBoss.getBossPattern();
			// Draw boss sprite
			drawEntity(omegaBoss, omegaBoss.getPositionX(), omegaBoss.getPositionY());

			// Draw dash path if showing
			if (omegaBoss.isShowingPath()) {
				int[] targetPoint = omegaBoss.getDashEndPoint();  // 플레이어 위치

				// Calculate boss center point
				int bossWidth = omegaBoss.getWidth();
				int bossHeight = omegaBoss.getHeight();
				int startX = omegaBoss.getPositionX() + bossWidth / 2;
				int startY = omegaBoss.getPositionY() + bossHeight / 2;
				int targetX = targetPoint[0];
				int targetY = targetPoint[1];

				// 방향 벡터 계산 후 길게 뻗어나가는 끝점 계산
				double dx = targetX - startX;
				double dy = targetY - startY;
				double len = Math.sqrt(dx * dx + dy * dy);

				if (len > 0) {
					dx /= len;
					dy /= len;

					// 충분히 긴 거리로 확장 (화면 밖까지)
					double extendDistance = 2000.0;
					int endX = (int) Math.round(startX + dx * extendDistance);
					int endY = (int) Math.round(startY + dy * extendDistance);

					// Draw path
					drawDashPath(startX, startY, endX, endY);
				}
			}
		}
		else {
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

	/**
	 * Draws boss dash path with dashed line style.
	 */
	public void drawDashPath(int startX, int startY, int endX, int endY) {
		Graphics g = backBuffer.getGraphics();
		Graphics2D g2d = (Graphics2D) g;

		Color originalColor = g2d.getColor();
		java.awt.Stroke originalStroke = g2d.getStroke();

		g2d.setColor(new Color(255, 0, 0, 180));

		float[] dashPattern = {10.0f, 5.0f};
		g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f,
				dashPattern, 0.0f));

		g2d.drawLine(startX, startY, endX, endY);
		drawArrowHead(g2d, startX, startY, endX, endY);

		g2d.setColor(originalColor);
		g2d.setStroke(originalStroke);
	}

	/**
	 * Draws an arrow head at the end of the path.
	 */
	private void drawArrowHead(Graphics2D g2d, int startX, int startY, int endX, int endY) {
		double angle = Math.atan2(endY - startY, endX - startX);
		int arrowSize = 12;

		int x1 = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
		int y1 = (int) (endY - arrowSize * Math.sin(angle - Math.PI / 6));
		int x2 = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
		int y2 = (int) (endY - arrowSize * Math.sin(angle + Math.PI / 6));

		g2d.drawLine(endX, endY, x1, y1);
		g2d.drawLine(endX, endY, x2, y2);
	}
}
