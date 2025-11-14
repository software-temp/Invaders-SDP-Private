package engine;

import java.util.List;
import java.util.Map;

/**
 * Implements the logic for calculating scores, including time-based bonuses.
 * @author Nam
 * */
public class LevelScore {

    /**
     * Calculates the time bonus based on the level's scoring criteria and the
     * time taken to finish.
     *
     * @param scoringCriteria
     * A map containing the scoring rules, parsed from the level JSON.
     * @param finishTimeInSeconds
     * The time in seconds it took to complete the level.
     * @return The calculated time bonus score.
     */
    @SuppressWarnings("unchecked")
    public final int calculateTimeBonus(final Map<String, Object> scoringCriteria, final int finishTimeInSeconds) {
        if (scoringCriteria == null || !scoringCriteria.containsKey("timeBonus")) {
            return 0;
        }

        List<Map<String, Object>> timeBonusTiers = (List<Map<String, Object>>) scoringCriteria.get("timeBonus");

        if (timeBonusTiers == null) {
            return 0;
        }

        for (Map<String, Object> tier : timeBonusTiers) {
            Object timeObj = tier.get("time");
            Object bonusObj = tier.get("bonus");

            if (timeObj instanceof Number && bonusObj instanceof Number) {
                int timeThreshold = ((Number) timeObj).intValue();
                int bonusValue = ((Number) bonusObj).intValue();

                if (finishTimeInSeconds <= timeThreshold) {
                    return bonusValue;
                }
            }
        }

        return 0;
    }

    /**
     * Calculates the final score by adding the time bonus to the base score.
     *
     * @param baseScore
     * The player's score before the time bonus.
     * @param scoringCriteria
     * A map containing the scoring rules, parsed from the level JSON.
     * @param finishTimeInSeconds
     * The time in seconds it took to complete the level.
     * @return The final total score.
     */
    public final int calculateFinalScore(final int baseScore, final Map<String, Object> scoringCriteria, final int finishTimeInSeconds) {
        int timeBonus = calculateTimeBonus(scoringCriteria, finishTimeInSeconds);
        return baseScore + timeBonus;
    }
}