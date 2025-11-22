package entity.pattern;

import engine.Cooldown;
import engine.Core;
import entity.HasBounds;

import java.awt.Point;
import java.util.Random;

/**
 * Manages the state and timing of the boss's area-wide (Apocalypse) attack pattern.
 * This class is part of the Model layer in MVC.
 */
public class ApocalypseAttackPattern extends BossPattern {
    /** 2-second warning cooldown */
    private Cooldown warningCooldown;
    /** attack animation cooldown */
    private static final int ATTACK_ANIMATION_DURATION = 500; // 0.5 sec
    private Cooldown attackAnimationCooldown;
    private long attackAnimationStartTime; // For calculating animation progress
    private boolean isAttacking = false; // Whether the attack animation is active

    /** Whether the warning (visual effect) is currently active */
    private boolean isWarning = false;
    /** Whether the pattern is currently active (for pausing boss movement) */
    private boolean isPatternActive = false;
    /** Index of the safe zone column (0-9) */
    private int safeZoneColumn = -1;
    /** Object for random generation */
    private Random random;

    protected HasBounds boss;

    public ApocalypseAttackPattern(HasBounds boss) {
        // Calls the BossPattern constructor.
        super(new Point(boss.getPositionX(), boss.getPositionY()));
        this.boss = boss;
        this.warningCooldown = Core.getVariableCooldown(2000, 0); // 2second
        this.attackAnimationCooldown = Core.getVariableCooldown(ATTACK_ANIMATION_DURATION, 0); // [추가] 1초
        this.random = new Random();
    }

    // Implementation of BossPattern abstract methods
    /**
     * Since GameModel directly checks the state of this pattern,
     * no specific logic is needed in the attack() method.
     */
    @Override
    public void attack() {
        // All logic is handled by GameModel polling this object's state
    }

    /**
     * The boss does not move during the Apocalypse pattern,
     * so the move() method is left empty.
     */
    @Override
    public void move() {
        // Boss does not move during this pattern
    }

    /**
     * Starts the pattern. (Called from Boss class)
     */
    public void start(int safeZoneCount) {
        if (isPatternActive) return; // Prevent duplicate execution if already active

        this.isWarning = true;
        this.isPatternActive = true;
        this.isAttacking = false;
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
        this.isAttacking = false;
        this.safeZoneColumn = -1;
    }

    // Method to start attack animation after warning ends
    /**
     * (Called by GameModel) Transitions from warning to attack animation.
     */
    public void beginAttackAnimation() {
        if (this.isWarning) { // Only execute during warning
            this.isWarning = false;
            this.isAttacking = true;
            this.attackAnimationCooldown.reset();
            this.attackAnimationStartTime = System.currentTimeMillis();
        }
    }

    // --- Getters for View and GameModel ---

    /**
     * (Used by View) Returns whether the warning visual effect should be drawn
     */
    public boolean isWarningActive() {
        return this.isWarning;
    }

    // Getter for attack animation state
    /**
     * (Used by View/GameModel) Returns whether the attack animation is playing.
     */
    public boolean isAttacking() {
        return this.isAttacking;
    }

    // Getter for attack animation progress
    /**
     * (Used by View) Returns the progress of the attack animation (0.0 to 1.0).
     */
    public float getAttackAnimationProgress() {
        if (!isAttacking()) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - this.attackAnimationStartTime;
        float progress = (float) elapsed / ATTACK_ANIMATION_DURATION;
        return Math.min(1.0f, progress); // Ensure it doesn't exceed 1.0
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

    // Getter for whether attack animation is finished
    /**
     * (Used by GameModel) Returns whether the 1-second attack animation is finished.
     */
    public boolean isAttackAnimationFinished() {
        return this.attackAnimationCooldown != null && this.attackAnimationCooldown.checkFinished();
    }
}
