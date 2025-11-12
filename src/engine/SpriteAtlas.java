package engine;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import engine.FileManager;
import engine.DrawManager.SpriteType;

public final class SpriteAtlas {

    private final Map<SpriteType, boolean[][]> spriteMap = new LinkedHashMap<>();

    public SpriteAtlas(FileManager fileManager) {
        try {
            spriteMap.put(SpriteType.Ship, new boolean[13][8]);
            spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
            spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
            spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
            spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
            spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[16][7]);
            spriteMap.put(SpriteType.Explosion, new boolean[13][7]);
            spriteMap.put(SpriteType.SoundOn, new boolean[15][15]);
            spriteMap.put(SpriteType.SoundOff, new boolean[15][15]);
            spriteMap.put(SpriteType.Item_Explode, new boolean[5][5]);
            spriteMap.put(SpriteType.Item_Slow, new boolean[5][5]);
            spriteMap.put(SpriteType.Item_Stop, new boolean[5][5]);
            spriteMap.put(SpriteType.Item_Push, new boolean[5][5]);
            spriteMap.put(SpriteType.Item_Shield, new boolean[5][5]);
            spriteMap.put(SpriteType.Item_Heal, new boolean[5][5]);
            spriteMap.put(SpriteType.FinalBoss1, new boolean[50][40]);
            spriteMap.put(SpriteType.FinalBoss2, new boolean[50][40]);
            spriteMap.put(SpriteType.FinalBossBullet,new boolean[3][5]);
            spriteMap.put(SpriteType.FinalBossDeath, new boolean[50][40]);
            spriteMap.put(SpriteType.OmegaBoss1, new boolean[32][14]);
            spriteMap.put(SpriteType.OmegaBoss2, new boolean[32][14]);
            spriteMap.put(SpriteType.OmegaBossDeath, new boolean[16][16]);
            fileManager.loadSprite(spriteMap);
        } catch (IOException e) {
            Core.getLogger().warning("[SpriteAtlas] Failed to load sprites: " + e.getMessage());
        }
    }

    public boolean[][] get(SpriteType type) {
        return spriteMap.get(type);
    }

    public Map<DrawManager.SpriteType, boolean[][]> getSpriteMap() {
        return java.util.Collections.unmodifiableMap(spriteMap);
    }

}
