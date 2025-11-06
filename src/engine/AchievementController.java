package engine;

import entity.IModel;
import screen.AchievementView;
import screen.IView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AchievementController implements IController{

    private IView achievementView;
    private final DrawManager drawManager;

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


    public AchievementController(AchievementView achievementView){
        this.achievementView=achievementView;

        achievements = new ArrayList<>();
        achievements.add(new Achievement("Beginner", "Clear level 1"));
        achievements.add(new Achievement("Intermediate", "Clear level 3"));
        achievements.add(new Achievement("Boss Slayer", "Defeat a boss"));
        achievements.add(new Achievement("Mr. Greedy", "Have more than 2000 coins"));
        achievements.add(new Achievement("First Blood", "Defeat your first enemy"));
        achievements.add(new Achievement("Bear Grylls", "Survive for 60 seconds"));
        achievements.add(new Achievement("Bad Sniper", "Under 80% accuracy"));
        achievements.add(new Achievement("Conqueror", "Clear the final level"));

        this.drawManager = Core.getDrawManager();
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



    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render() {
        ((AchievementView) achievementView).draw(achievements);
    }
}
