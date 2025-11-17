package entity.pattern;

import engine.Cooldown;
import engine.Core;
import java.util.Random;

/**
 * Manages the state and timing of the boss's area-wide (Apocalypse) attack pattern.
 * This class is part of the Model layer in MVC.
 */
public class ApocalypseAttackPattern {
    /** 2-second warning cooldown */
    private Cooldown warningCooldown;
    /** Whether the warning (visual effect) is currently active */
    private boolean isWarning = false;
    /** Whether the pattern is currently active (for pausing boss movement) */
    private boolean isPatternActive = false;
    /** Index of the safe zone column (0-9) */
    private int safeZoneColumn = -1;
    /** Object for random generation */
    private Random random;

    public ApocalypseAttackPattern() {
        this.warningCooldown = Core.getVariableCooldown(2000, 0); // 2second
        this.random = new Random();
    }

    /**
     * Starts the pattern. (Called from Boss class)
     */
    public void start(int safeZoneCount) {
        if (isPatternActive) return; // Prevent duplicate execution if already active

        this.isWarning = true;
        this.isPatternActive = true;
        this.warningCooldown.reset();

        // Select one of the 10 columns randomly as the safe zone
        this.safeZoneColumn = random.nextInt(10); // 0 ~ 9
    }

    /**
     * Finishes and resets all pattern states. (Called from GameModel)
     */
    public void finishPattern() {
        this.isWarning = false;
        this.isPatternActive = false;
        this.safeZoneColumn = -1;
    }

    // --- Getters for View and GameModel ---

    /**
     * (Used by View) Returns whether the warning visual effect should be drawn
     */
    public boolean isWarningActive() {
        return this.isWarning;
    }

    /**
     * (Used by GameModel/Boss) Returns whether the pattern is active (boss should be paused)
     */
    public boolean isPatternActive() {
        return this.isPatternActive;
    }

    /**
     * (Used by View) Returns the index of the safe zone column (0-9)
     */
    public int getSafeZoneColumn() {
        return this.safeZoneColumn;
    }

    /**
     * (Used by GameModel) Returns whether the 2-second warning time is finished
     */
    public boolean isWarningFinished() {
        return this.warningCooldown.checkFinished();
    }
}
