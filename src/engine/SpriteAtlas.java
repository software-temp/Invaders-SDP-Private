package engine;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import engine.FileManager;
import engine.DrawManager.SpriteType;

public final class SpriteAtlas {

    private final Map<SpriteType, Color[][]> spriteMap = new LinkedHashMap<>();

    public SpriteAtlas(FileManager fileManager) {
        try {
            spriteMap.put(SpriteType.ShipP1, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP2, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP1Move, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP2Move, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP2Explosion1, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP2Explosion2, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP2Explosion3, new Color[13][13]);
            spriteMap.put(SpriteType.Life, new Color[8][8]);
            spriteMap.put(SpriteType.ShipP1Explosion1, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP1Explosion2, new Color[13][13]);
            spriteMap.put(SpriteType.ShipP1Explosion3, new Color[13][13]);
            spriteMap.put(SpriteType.Bullet, new Color[3][5]);
            spriteMap.put(SpriteType.EnemyBullet, new Color[3][5]);
            spriteMap.put(SpriteType.EnemyShipA1, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipA2, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipB1, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipB2, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipC1, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipC2, new Color[12][8]);
            spriteMap.put(SpriteType.EnemyShipSpecial, new Color[16][16]);
            spriteMap.put(SpriteType.EnemyShipSpecialLeft, new Color[16][16]);
            spriteMap.put(SpriteType.EnemySpecialExplosion, new Color[16][16]);
            spriteMap.put(SpriteType.Explosion, new Color[13][13]);
            spriteMap.put(SpriteType.SoundOn, new Color[15][15]);
            spriteMap.put(SpriteType.SoundOff, new Color[15][15]);
            spriteMap.put(SpriteType.Item_Explode, new Color[10][10]);
            spriteMap.put(SpriteType.Item_Slow, new Color[13][18]);
            spriteMap.put(SpriteType.Item_Stop, new Color[10][10]);
            spriteMap.put(SpriteType.Item_Push, new Color[10][10]);
            spriteMap.put(SpriteType.Item_Shield, new Color[10][10]);
            spriteMap.put(SpriteType.Item_Heal, new Color[10][10]);
            spriteMap.put(SpriteType.Shield, new Color[30][30]);
            spriteMap.put(SpriteType.FinalBoss1, new Color[50][40]);
            spriteMap.put(SpriteType.FinalBoss2, new Color[50][40]);
            spriteMap.put(SpriteType.FinalBossBullet,new Color[3][5]);
            spriteMap.put(SpriteType.FinalBossDeath, new Color[50][40]);
            spriteMap.put(SpriteType.OmegaBoss1, new Color[32][32]);
            spriteMap.put(SpriteType.OmegaBoss2, new Color[32][32]);
            spriteMap.put(SpriteType.OmegaBossMoving1, new Color[32][32]);
            spriteMap.put(SpriteType.OmegaBossMoving2, new Color[32][32]);
            spriteMap.put(SpriteType.OmegaBossDeath, new Color[16][16]);
            spriteMap.put(SpriteType.OmegaBoss100, new Color[96][81]);
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

    public Color[][] get(SpriteType type) {
        return spriteMap.get(type);
    }

    public Map<DrawManager.SpriteType, Color[][]> getSpriteMap() {
        return java.util.Collections.unmodifiableMap(spriteMap);
    }
    private Color[][] mirrorSprite(Color[][] original) {
        if (original == null || original.length == 0) return null;
        int w = original.length;
        int h = original[0].length;
        Color[][] mirrored = new Color[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color c = original[w - 1 - i][j];
                // 널 값 대비
                mirrored[i][j] = (c != null) ? c : new Color(0, 0, 0, 0);
            }
        }
        return mirrored;
    }

}
