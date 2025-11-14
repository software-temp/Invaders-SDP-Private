package screen;

import java.awt.event.KeyEvent;
import java.util.List;

import engine.Achievement;
import engine.AchievementManager;

/**
 * Implements the achievement screen, which displays the player's achievements.
 */
public class AchievementScreen extends Screen {

    /**
     * Constructor for the AchievementScreen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second.
     */
    public AchievementScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.returnCode = 1; // Default return code
    }

    /**
     * Initializes the screen elements.
     */
    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * Runs the screen's main loop.
     *
     * @return The screen's return code.
     */
    @Override
    public int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Updates the screen's state.
     */
    @Override
    protected void update() {
        super.update();
        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.isRunning = false;
        }
    }

    /**
     * Draws the achievements on the screen.
     */
    private void draw() {
        drawManager.initDrawing(this.width, this.height);
        List<Achievement> achievements = AchievementManager.getInstance().getAchievements();
        drawManager.getUIRenderer().drawAchievements(this.width,this.height, achievements);
        drawManager.completeDrawing();
    }
}
