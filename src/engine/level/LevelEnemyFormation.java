package engine.level;

import java.util.Map;

public class LevelEnemyFormation {
    private int formationWidth;
    private int formationHeight;
    private int baseSpeed;
    private int shootingFrecuency;

    public LevelEnemyFormation(Map<String, Object> map) {
        this.formationWidth = ((Number) map.get("formationWidth")).intValue();
        this.formationHeight = ((Number) map.get("formationHeight")).intValue();
        this.baseSpeed = ((Number) map.get("baseSpeed")).intValue();
        this.shootingFrecuency = ((Number) map.get("shootingFrecuency")).intValue();
    }

    // Getters

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
}
