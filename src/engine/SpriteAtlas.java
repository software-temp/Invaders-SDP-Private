package engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import engine.DrawManager.SpriteType;

public final class SpriteAtlas {

    private Map<SpriteType, BufferedImage> spriteMap = new LinkedHashMap<>();

    public SpriteAtlas(FileManager fileManager) {
        try {
            spriteMap = new LinkedHashMap<SpriteType, BufferedImage>();
            spriteMap.put(SpriteType.ShipP1, new BufferedImage(25,31,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP2, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP1Move, new BufferedImage(25,43,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP2Move, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP2Explosion1, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP2Explosion2, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP2Explosion3, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Life, new BufferedImage(8,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP1Explosion1, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP1Explosion2, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.ShipP1Explosion3, new BufferedImage(13,13,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Bullet, new BufferedImage(3,5,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyBullet, new BufferedImage(3,5,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipA1, new BufferedImage(25,25,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipA2, new BufferedImage(25,25,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipB1, new BufferedImage(12,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipB2, new BufferedImage(12,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipC1, new BufferedImage(12,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipC2, new BufferedImage(12,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipSpecial, new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemyShipSpecialLeft, new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.EnemySpecialExplosion, new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Explosion, new BufferedImage(25,25,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.SoundOn, new BufferedImage(15,15,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.SoundOff, new BufferedImage(15,15,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Explode, new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Slow, new BufferedImage(13,8,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Stop, new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Push, new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Shield, new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Item_Heal, new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.Shield, new BufferedImage(30,30,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.FinalBoss1, new BufferedImage(50,40,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.FinalBoss2, new BufferedImage(50,40,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.FinalBossBullet,new BufferedImage(3,5,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.FinalBossDeath, new BufferedImage(50,40,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBoss1, new BufferedImage(43,41,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBoss2, new BufferedImage(43,41,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBossHitting, new BufferedImage(43,41,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBossHitting1, new BufferedImage(43,41,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBossMoving1, new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBossMoving2, new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBossDeath, new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB));
            spriteMap.put(SpriteType.OmegaBoss100, new BufferedImage(96,81,BufferedImage.TYPE_INT_ARGB));
            fileManager.loadSprite(spriteMap);
            //여기서부터는 대칭 스프라이트
            spriteMap.put(SpriteType.OmegaBoss3, mirrorSprite(spriteMap.get(SpriteType.OmegaBoss1)));
            spriteMap.put(SpriteType.OmegaBoss4, mirrorSprite(spriteMap.get(SpriteType.OmegaBoss2)));
            spriteMap.put(SpriteType.OmegaBossMoving3, mirrorSprite(spriteMap.get(SpriteType.OmegaBossMoving1)));
            spriteMap.put(SpriteType.OmegaBossMoving4, mirrorSprite(spriteMap.get(SpriteType.OmegaBossMoving2)));
            spriteMap.put(SpriteType.OmegaBoss101, mirrorSprite(spriteMap.get(SpriteType.OmegaBoss100)));
            fileManager.loadSprite(spriteMap);
        } catch (IOException e) {
            Core.getLogger().warning("[SpriteAtlas] Failed to load sprites: " + e.getMessage());
        }
    }

    public BufferedImage get(SpriteType type) {
        return spriteMap.get(type);
    }

    public Map<DrawManager.SpriteType, BufferedImage> getSpriteMap() {
        return java.util.Collections.unmodifiableMap(spriteMap);
    }

    private BufferedImage mirrorSprite(BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();

        BufferedImage mirrored = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mirrored.createGraphics();

        // srcX1, srcY1, srcX2, srcY2 순서로 반전됨
        g.drawImage(original,
                0, 0, w, h,   // destination (그릴 위치)
                w, 0, 0, h,   // source (원본 픽셀 what to draw)
                null);

        g.dispose();
        return mirrored;
    }

}
