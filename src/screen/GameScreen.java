package screen;

import engine.Core;
import engine.GameState;
import engine.level.Level;
import entity.GameModel;



/**
 * Implements the game screen, where the action happens.
 * This class acts as the CONTROLLER in the MVC pattern.
 * It manages the game loop, handles input, and coordinates
 * the GameModel (state/logic) and GameView (drawing).
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen {

    /** Height of the interface separation line. */
    public static final int SEPARATION_LINE_HEIGHT = 45;
    /** Height of the items separation line (above items). */
    public static final int ITEMS_SEPARATION_LINE_HEIGHT = 400;
    /** Returns the Y-coordinate of the bottom boundary for enemies (above items HUD) */
    public static int getItemsSeparationLineHeight() {
        return ITEMS_SEPARATION_LINE_HEIGHT;
    }

    /** Current level data (direct from Level system). */
    private Level currentLevel;
    /** Checks if a bonus life is received. */
    private boolean bonusLife;
    /** Maximum number of lives. */
    private int maxLives;
    /** Current game state. */
    private GameState gameState;

    /** The Model component */
    private GameModel model;
    /** The View component */
    private GameView view;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState
     * Current game state.	 * @param level
     * Current level settings.
     * @param bonusLife
     * Checks if a bonus life is awarded this level.
     * @param maxLives
     * Maximum number of lives.
     * @param width
     * Screen width.
     * @param height
     * Screen height.
     * @param fps
     * Frames per second, frame rate at which the game is run.
     */
    public GameScreen(final GameState gameState,
                      final Level level, final boolean bonusLife, final int maxLives,
                      final int width, final int height, final int fps) {
        super(width, height, fps);

        this.currentLevel = level;
        this.bonusLife = bonusLife;
        this.maxLives = maxLives;
        this.gameState = gameState;
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        super.initialize();

        // Create Model and View
        this.model = new GameModel(this.gameState, this.currentLevel, this.bonusLife, this.maxLives, this.width, this.height, this);
        this.view = new GameView(this.model, this.drawManager, this.width, this.height);

        // Initialize the model's state
        this.model.initialize();

        this.inputDelay = Core.getCooldown(GameModel.INPUT_DELAY);
        this.inputDelay.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        // Calculate final score from the model
        int finalScore = this.model.calculateFinalScore();
        this.logger.info("Screen cleared with a score of " + finalScore);

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     * This is the main Controller loop.
     */
    protected final void update() {
        super.update();

        // Check if input delay is over and game is not finished
        if (this.inputDelay.checkFinished() && !this.model.isLevelFinished()) {

            if (!this.model.isTimerRunning()) {
                this.model.startTimer();
            }

            // 1. (Controller) Process user input and tell Model to update
            // Player 1 Input
            // (Get player object from model to check status)
            if (model.getLivesP1() > 0 && model.getShip() != null && !model.getShip().isDestroyed()) {
                // (Controller detects input and sends a 'command' to the model)
                if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_D))
                    model.playerMove(1, "RIGHT");
                if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_A))
                    model.playerMove(1, "LEFT");
                if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_W))
                    model.playerMove(1, "UP");
                if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_S))
                    model.playerMove(1, "DOWN");
                if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_SPACE))
                    model.playerFire(1);
            }

            // Player 2 Input
            if (model.getShipP2() != null && model.getLivesP2() > 0 && !model.getShipP2().isDestroyed()) {
                if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_RIGHT))
                    model.playerMove(2, "RIGHT");
                if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_LEFT))
                    model.playerMove(2, "LEFT");
                if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_UP))
                    model.playerMove(2, "UP");
                if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_DOWN))
                    model.playerMove(2, "DOWN");
                if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_ENTER))
                    model.playerFire(2);
            }

            // 2. (Controller) Tell Model to update all game logic
            this.model.updateGameWorld();
        }

        // Update elapsed time (if timer is running)
        if (this.model.isTimerRunning()) {
            this.model.updateElapsedTime();
        }

        // 3. (Controller) Tell View to draw the current Model state
        this.view.draw(this);

        // 4. (Controller) Check Model state for game over
        if (this.model.isGameOver() && !this.model.isLevelFinished()) {
            this.model.setGameOver();
        }

        // 5. (Controller) Check Model state for level finished
        if (this.model.isLevelFinished() && this.model.getScreenFinishedCooldown().checkFinished()) {
            this.model.processLevelCompletion();
            this.isRunning = false;
        }
    }

    /**
     * Returns a GameState object representing the status of the game.
     * (Passthrough to the model)
     *
     * @return Current game state.
     */
    public final GameState getGameState() {
        return this.model.getGameState();
    }
}