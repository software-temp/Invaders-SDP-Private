package engine.renderer;

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
    private final double scale;

    public EntityRenderer(Map<SpriteType, Color[][]> spriteMap, BackBuffer backBuffer, double scale) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
        this.scale = scale;
    }

    /** Draws a single entity on the back buffer. */
    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
        Color[][] image = spriteMap.get(entity.getSpriteType());
        Graphics g = backBuffer.getGraphics();
        g.setColor(entity.getColor());
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (entity.getSpriteType() == SpriteType.SoundOn || entity.getSpriteType() == SpriteType.SoundOff){
                    if (image[i][j].getAlpha() > 0 && image[i][j] != null) {
                        g.setColor(entity.getColor());
                    } else {
                        continue;
                    }
                }
                else {
                    g.setColor(image[i][j]);
                }
                int pixelSize = (int) Math.max(1, 2 * scale);
                int scaledX = positionX + (int)(i * pixelSize);
                int scaledY = positionY + (int)(j * pixelSize);
                g.fillRect(scaledX, scaledY, pixelSize, pixelSize);
            }
        }
    }
    public void drawShield(int shipPositionX, int shipPositionY, double ratio){
        int alpha = (int) (255 * ratio);
        if (alpha < 30) alpha = 30;
        Color[][] image = spriteMap.get(SpriteType.Shield);
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                Color baseColor = image[i][j];
                // 완전 투명 픽셀은 무시
                if (baseColor.getAlpha() == 0) continue;
                // 알파값을 ratio 기반으로 새로 계산
                Color blendedColor = new Color(
                        baseColor.getRed(),
                        baseColor.getGreen(),
                        baseColor.getBlue(),
                        Math.min(alpha, baseColor.getAlpha())
                );
                backBuffer.getGraphics().setColor(blendedColor);
                backBuffer.getGraphics().drawRect(shipPositionX - 4 + i * 2, shipPositionY + j * 2, 1, 1);
            }
        }
    }
    public void drawLife(final int positionX, final int positionY){
        Color[][] image = spriteMap.get(SpriteType.Life);
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                backBuffer.getGraphics().setColor(image[i][j]);
                backBuffer.getGraphics().drawRect(positionX + i * 2, positionY + j * 2, 1, 1);
            }
        }
    }

}
