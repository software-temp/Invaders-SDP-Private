package engine.level;

import java.util.Map;

public class CompletionBonus {
    private int currency;

    public CompletionBonus(Map<String, Object> map) {
        this.currency = ((Number) map.get("currency")).intValue();
    }

    public int getCurrency() {
        return currency;
    }
}
