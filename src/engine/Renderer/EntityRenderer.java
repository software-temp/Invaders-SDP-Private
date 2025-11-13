package engine.Renderer;

import java.awt.*;
import java.util.Map;

import engine.BackBuffer;
import entity.Entity;
import engine.DrawManager.SpriteType;

/**
 * Handles rendering of all game entities using the shared back buffer.
 * Acts as a sub-view in MVC architecture.
 */
public final class EntityRenderer {

    private final Map<SpriteType, Color[][]> spriteMap;
    private final BackBuffer backBuffer;
    private final double scaleX;
    private final double scaleY;

    public EntityRenderer(Map<SpriteType, Color[][]> spriteMap, BackBuffer backBuffer, double scaleX, double scaleY) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /** Draws a single entity on the back buffer. */
//    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
//        boolean[][] image = spriteMap.get(entity.getSpriteType());
//        Graphics g = backBuffer.getGraphics();
//
//        g.setColor(entity.getColor());
//
//        for (int i = 0; i < image.length; i++) {
//            for (int j = 0; j < image[i].length; j++) {
//                if (image[i][j]) {
//                    // 스케일 적용된 좌표 계산
//                    int scaledX = (int) ((positionX + i * 2) * this.scaleX);
//                    int scaledY = (int) ((positionY + j * 2) * this.scaleY);
//
//                    // 스케일 적용된 픽셀 크기
//                    int pixelWidth = (int) Math.max(1, 2 * this.scaleX);
//                    int pixelHeight = (int) Math.max(1, 2 * this.scaleY);
//                    g.fillRect(scaledX, scaledY, pixelWidth, pixelHeight);
//                }
//            }
//        }
//    }
    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
        Color[][] image = spriteMap.get(entity.getSpriteType());
        Graphics g = backBuffer.getGraphics();
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (entity.getSpriteType() == SpriteType.SoundOn || entity.getSpriteType() == SpriteType.SoundOff){
                    if (image[i][j].getAlpha() > 0 && image[i][j] != null) {
                        g.setColor(entity.getColor());
                    } else {
                        continue;
                    }
                } else {
                    g.setColor(image[i][j]);
                }
                int scaledX = (int) ((positionX + i * 2) * this.scaleX);
                int scaledY = (int) ((positionY + j * 2) * this.scaleY);

                // 스케일 적용된 픽셀 크기
                int pixelWidth = (int) Math.max(1, 2 * this.scaleX);
                int pixelHeight = (int) Math.max(1, 2 * this.scaleY);
                g.fillRect(scaledX, scaledY, pixelWidth, pixelHeight);
            }
        }
    }
}
