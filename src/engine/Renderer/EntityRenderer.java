package engine.Renderer;

import java.awt.Graphics;
import java.util.Map;

import engine.BackBuffer;
import entity.Entity;
import engine.DrawManager.SpriteType;

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
                    // 스케일 적용된 좌표 계산
                    int scaledX = (int) ((positionX + i * 2));
                    int scaledY = (int) ((positionY + j * 2));
                    int pixelSize = (int) Math.max(1, 2 * scale);
                    g.fillRect(scaledX, scaledY, pixelSize, pixelSize);
                }
            }
        }
    }
}
