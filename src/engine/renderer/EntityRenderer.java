package engine.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import engine.BackBuffer;
import entity.Entity;
import engine.DrawManager.SpriteType;

/**
 * Handles rendering of all game entities using the shared back buffer.
 * Acts as a sub-view in MVC architecture.
 */
public final class EntityRenderer {

    private final Map<SpriteType, BufferedImage> spriteMap;
    private final BackBuffer backBuffer;
    private final double scale;

    public EntityRenderer(Map<SpriteType, BufferedImage> spriteMap, BackBuffer backBuffer, double scale) {
        this.spriteMap = spriteMap;
        this.backBuffer = backBuffer;
        this.scale = scale;
    }

    /** Draws a single entity on the back buffer. */
    public void drawEntity(final Entity entity, final int positionX, final int positionY) {
        Graphics2D g2d = (Graphics2D) backBuffer.getGraphics();
        BufferedImage img = spriteMap.get(entity.getSpriteType());
        if (img == null) {
            return;
        }
        int originalW = img.getWidth();
        int originalH = img.getHeight();
        int scaledW = (int) (originalW * scale * 2);
        int scaledH = (int) (originalH * scale * 2);

        if (entity.getSpriteType() == SpriteType.SoundOn ||
                entity.getSpriteType() == SpriteType.SoundOff) {

            img = tintImage(img, entity.getColor());
        }
        int drawX = positionX; // 가운데 정렬: int drawX = positionX- scaledW / 2
        int drawY = positionY; // 가운데 정렬: int drawY = positionY - scaledH / 2
        g2d.drawImage(img, drawX, drawY, scaledW, scaledH, null);
    }

    private BufferedImage tintImage(BufferedImage src, Color color) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage tinted = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = src.getRGB(x, y);

                // 알파 0이면 투명 (그리지 않음)
                if ((argb >> 24) == 0) continue;

                // 새 색상 적용 (알파는 유지)
                int alpha = (argb >> 24) & 0xFF;
                int rgb = (color.getRGB() & 0x00FFFFFF) | (alpha << 24);
                tinted.setRGB(x, y, rgb);
            }
        }
        return tinted;
    }
    public void drawShield(int shipPositionX, int shipPositionY, double ratio) {
        BufferedImage shield = spriteMap.get(SpriteType.Shield);
        if (shield == null) return;

        // 원본 크기
        int w = shield.getWidth();
        int h = shield.getHeight();

        // 화면 스케일 적용
        int scaledW = (int) (w * scale * 2);
        int scaledH = (int) (h * scale * 2);

        // 투명도 계산 (ratio 기반)
        int alpha = (int) (255 * ratio);
        if (alpha < 30) alpha = 30; // 최소 투명도

        // 쉴드 블렌딩
        BufferedImage tinted = tintAlpha(shield, alpha);
        // 쉴드를 약간 배 밖으로 나오게 하고 싶으면 offset 조절
        Graphics2D g2d = (Graphics2D) backBuffer.getGraphics();
        g2d.drawImage(tinted, shipPositionX - 4, shipPositionY, scaledW, scaledH, null);
    }
    private BufferedImage tintAlpha(BufferedImage src, int alpha) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = src.getRGB(x, y);

                int originalAlpha = (argb >> 24) & 0xFF;
                if (originalAlpha == 0) continue; // 완전 투명

                int newAlpha = Math.min(alpha, originalAlpha);

                int rgb = (argb & 0x00FFFFFF) | (newAlpha << 24);

                result.setRGB(x, y, rgb);
            }
        }

        return result;
    }
    public void drawLife(final int positionX, final int positionY){
        Graphics2D g2d = (Graphics2D) backBuffer.getGraphics();
        BufferedImage image = spriteMap.get(SpriteType.Life);
        int scaledW = (int) (image.getWidth() * scale * 2);
        int scaledH = (int) (image.getHeight() * scale * 2);
        g2d.drawImage(image, positionX, positionY, scaledW, scaledH, null);
    }


}
