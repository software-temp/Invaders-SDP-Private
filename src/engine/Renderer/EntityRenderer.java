package engine.Renderer;

import java.awt.Graphics;
import java.awt.Color;
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

    /** BlackHole Color */
    private static final Color BLACK_HOLE_COLOR = new Color(200, 0, 255);

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

    /** Draw circle for pull_attack pattern */
    public void drawBlackHole(final int cx, final int cy, final int radius, final boolean isBlackHoleActive){
        if(isBlackHoleActive){
            int x = cx - radius/2;
            int y = cy - radius/2;

            Graphics g = backBuffer.getGraphics();
            g.setColor(BLACK_HOLE_COLOR);
            g.drawOval(x, y, radius, radius);
        }
    }
}
