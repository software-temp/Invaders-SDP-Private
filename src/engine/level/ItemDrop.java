package engine.level;

import java.util.Map;

public class ItemDrop {
    private String enemyType;
    private String bossId;
    private String itemId;
    private double dropChance;

    public ItemDrop(Map<String, Object> map) {
        this.enemyType = (String) map.get("enemyType");
        this.bossId = (String) map.get("bossId");
        this.itemId = (String) map.get("itemId");
        this.dropChance = ((Number) map.get("dropChance")).doubleValue();
    }

    // Getters

    public String getEnemyType() {
        return enemyType;
    }

    public String getBossId() {
        return bossId;
    }

    public String getItemId() {
        return itemId;
    }

    public double getDropChance() {
        return dropChance;
    }
}
