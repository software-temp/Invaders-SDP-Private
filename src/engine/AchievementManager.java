package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all game achievements (including their state, unlocking logic, and persistence).
 */
public class AchievementManager {
    /** Stores the single instance of the AchievementManager. */
    private static AchievementManager instance;
    /** List of all achievements in the game. */
    private List<Achievement> achievements;
    /** Counter for the total number of shots fired by the player. */
    private int shotsFired = 0;
    /** Counter for the total number of shots that hit an enemy. */
    private int shotsHit = 0;
    /** Flag to ensure the 'First Blood' achievement is unlocked only once. */
    private boolean firstKillUnlocked = false;
    /** Flag to ensure the 'Bad Sniper' achievement is unlocked only once. */
    private boolean sniperUnlocked = false;
    /** Flag to ensure the 'Bear Grylls' achievement is unlocked only once. */
    private boolean survivorUnlocked = false;

    /**
     * Private constructor to initialize the achievement list and load their status.
     * Part of the Singleton pattern.
     */
    private AchievementManager() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("Beginner", "Clear level 1"));
        achievements.add(new Achievement("Intermediate", "Clear level 3"));
        achievements.add(new Achievement("Boss Slayer", "Defeat a boss"));
        achievements.add(new Achievement("Mr. Greedy", "Have more than 2000 coins"));
        achievements.add(new Achievement("First Blood", "Defeat your first enemy"));
        achievements.add(new Achievement("Bear Grylls", "Survive for 60 seconds"));
        achievements.add(new Achievement("Bad Sniper", "Under 80% accuracy"));
        achievements.add(new Achievement("Conqueror", "Clear the final level"));

        loadAchievements();
    }

    /**
     * Provides the global access point to the AchievementManager instance.
     *
     * @return The singleton instance of AchievementManager.
     */
    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    /**
     * Gets the list of all achievements.
     *
     * @return A list of all achievements.
     */
    public List<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * Unlocks a specific achievement by name.
     * If the achievement is found and not already unlocked, it marks it as unlocked
     * and saves the updated status.
     *
     * @param name The name of the achievement to unlock.
     */
    public void unlockAchievement(String name) {
        for (Achievement achievement : achievements) {
            if (achievement.getName().equals(name) && !achievement.isUnlocked()) {
                achievement.unlock();

                saveAchievements();
                break;
            }
        }
    }

    /**
     * Handles game events when an enemy is defeated.
     * Checks for and unlocks achievements related to enemy kills and accuracy.
     */
    public void onEnemyDefeated() {
        if (!firstKillUnlocked) {
            unlockAchievement("First Blood");
            firstKillUnlocked = true;
        }

        shotsHit++;

        if (!sniperUnlocked && shotsFired > 5) {
            double accuracy = (shotsHit / (double) shotsFired) * 100.0;
            if (accuracy <= 80.0) {
                unlockAchievement("Bad Sniper");
                sniperUnlocked = true;
            }
        }
    }

    /**
     * Handles game events related to elapsed time.
     * Checks for and unlocks achievements related to survival time.
     *
     * @param elapsedSeconds The total number of seconds elapsed in the game.
     */
    public void onTimeElapsedSeconds(int elapsedSeconds) {
        if (!survivorUnlocked && elapsedSeconds >= 60) {
            unlockAchievement("Bear Grylls");
            survivorUnlocked = true;
        }
    }

    /**
     * Handles the game event when a shot is fired.
     * Increments the counter for shots fired.
     */
    public void onShotFired() {
        shotsFired++;
    }

    /**
     * Loads achievement status from file and updates the current achievement list.
     * <p>
     * Requests the FileManager to load saved achievement data, then updates
     * each achievement's unlocked state accordingly.
     * </p>
     *
     * @throws RuntimeException
     *             If an I/O error occurs while loading achievements.
     */
    public void loadAchievements() {
        try {
            // Ask FileManager to load saved achievement status
            java.util.Map<String, Boolean> unlockedStatus = Core.getFileManager().loadAchievements();
            // Update the state of each achievement based on the loaded data.
            for (Achievement achievement : achievements) {
                if (unlockedStatus.getOrDefault(achievement.getName(), false)) {
                    achievement.unlock();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load achievement file! Creating a new one.");
            // If file loading fails, attempt an initial save.
            saveAchievements();
        }
    }
    /**
     * Saves the current achievement status to file.
     * <p>
     * Requests the FileManager to write all current achievements to disk.
     * </p>
     *
     * @throws RuntimeException
     *             If an I/O error occurs while saving achievements.
     */
    private void saveAchievements() {
        try {
            // Ask FileManager to save all current achievement data
            Core.getFileManager().saveAchievements(achievements);
        } catch (IOException e) {
            System.err.println("Failed to save achievement file!");
            e.printStackTrace();
        }
    }


}
