package engine.level;

import java.util.Map;

public class EnemyType {
    private String type;
    private int count;

    public EnemyType(Map<String, Object> map) {
        this.type = (String) map.get("type");
        this.count = ((Number) map.get("count")).intValue();
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
