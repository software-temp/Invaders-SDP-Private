package engine.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.Color;

/**
 * Represents the settings for a single level.
 */
public class Level {

    // Simple fields for backward compatibility with hardcoded levels
    private int level;
    private int formationWidth;
    private int formationHeight;
    private int baseSpeed;
    private int shootingFrecuency;

    // New fields for the complex JSON structure
    private String levelName;
    private String specialGimmick;
    private String unlockCondition;
    
    // Fields moved up from the removed Wave class
    private LevelEnemyFormation enemyFormation;
    private List<EnemyType> enemyTypes;
    private List<ItemDrop> itemDrops;
    private String bossId;

    private CompletionBonus completionBonus;
    private String achievementTrigger;


    /**
     * Constructor for hardcoding/testing.
     * Keeps the game working with the old simple level structure.
     */
    public Level(int level, int formationWidth, int formationHeight, int baseSpeed, int shootingFrecuency) {
        this.level = level;
        this.formationWidth = formationWidth;
        this.formationHeight = formationHeight;
        this.baseSpeed = baseSpeed;
        this.shootingFrecuency = shootingFrecuency;
    }

    /**
     * Constructor for creating from a map (e.g., from JSON).
     * @param map The map containing level data.
     */
    @SuppressWarnings("unchecked")
    public Level(Map<String, Object> map) {
        // Parsing simple fields
        this.level = ((Number) map.get("level")).intValue();
        this.levelName = (String) map.get("levelName");
        this.achievementTrigger = (String) map.get("achievementTrigger");
        this.specialGimmick = (String) map.get("specialGimmick");
        this.unlockCondition = (String) map.get("unlockCondition");

        // Parsing nested objects and lists (previously in Wave)
        if (map.get("enemyFormation") != null) {
            this.enemyFormation = new LevelEnemyFormation((Map<String, Object>) map.get("enemyFormation"));
        }
        if (map.get("enemyTypes") != null) {
            this.enemyTypes = new ArrayList<>();
            for (Map<String, Object> enemyTypeMap : (List<Map<String, Object>>) map.get("enemyTypes")) {
                this.enemyTypes.add(new EnemyType(enemyTypeMap));
            }
        }
        if (map.get("itemDrops") != null) {
            this.itemDrops = new ArrayList<>();
            for (Map<String, Object> itemDropMap : (List<Map<String, Object>>) map.get("itemDrops")) {
                this.itemDrops.add(new ItemDrop(itemDropMap));
            }
        }
        this.bossId = (String) map.get("bossId");

        if (map.get("completionBonus") != null) {
            this.completionBonus = new CompletionBonus((Map<String, Object>) map.get("completionBonus"));
        }

        // Fallback for simple fields from the enemy formation
        // This maintains compatibility with the old GameSettings logic
        if (this.enemyFormation != null) {
            this.formationWidth = this.enemyFormation.getFormationWidth();
            this.formationHeight = this.enemyFormation.getFormationHeight();
            this.baseSpeed = this.enemyFormation.getBaseSpeed();
            this.shootingFrecuency = this.enemyFormation.getShootingFrecuency();
        }
    }

    // Getters for all fields

    public int getLevel() {
        return level;
    }

    public int getFormationWidth() {
        return formationWidth;
    }

    public int getFormationHeight() {
        return formationHeight;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public int getShootingFrecuency() {
        return shootingFrecuency;
    }

    public String getLevelName() {
        return levelName;
    }

    public LevelEnemyFormation getEnemyFormation() {
        return enemyFormation;
    }

    public List<EnemyType> getEnemyTypes() {
        return enemyTypes;
    }

    public List<ItemDrop> getItemDrops() {
        return itemDrops;
    }

    public String getBossId() {
        return bossId;
    }

    public CompletionBonus getCompletionBonus() {
        return completionBonus;
    }

    public String getAchievementTrigger() {
        return achievementTrigger;
    }

    public String getSpecialGimmick() {
        return specialGimmick;
    }

    public String getUnlockCondition() {
        return unlockCondition;
    }

    public Color getColorForLevel() {
        switch (this.level) {
            case 1: return new Color(0x3DDC84); // green
            case 2: return new Color(0x00BCD4); // cyan
            case 3: return new Color(0xFF4081); // pink
            case 4: return new Color(0xFFC107); // amber
            case 5: return new Color(0x9C27B0); // purple
            case 6: return new Color(0xFF5722); // deep orange
            case 7: return new Color(0x8BC34A); // light green
            case 8: return new Color(0x03A9F4); // light blue
            case 9: return new Color(0xE91E63); // magenta
            case 10: return new Color(0x607D8B); // blue gray
            default: return Color.WHITE;
        }
    }
}
