package screen;

import engine.Cooldown;
import engine.DTO.HUDInfoDTO;
import engine.GameState;
import engine.level.Level;
import entity.GameConstant;
import entity.GameModel;

import java.awt.event.KeyEvent;

/**
 * Implements the game screen, where the action happens.
 * Acts as the CONTROLLER in the MVC pattern.
 * - Controls game flow, inputs, and timing
 * - Updates the GameModel (Model)
 * - Passes data to GameView (View) via HUDInfoDTO
 */
public class GameScreen extends Screen {

    /** Current level data. */
    private final Level currentLevel;
    private final boolean bonusLife;
    private final int maxLives;
    private final GameState gameState;

    /** MVC Components */
    private GameModel model;   // Model
    private GameView view;     // View

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
     * Initializes the Model and View.
     */
    @Override
    public final void initialize() {
        super.initialize();

        // Create Model and View
        this.model = new GameModel(
                this.gameState, this.currentLevel,
                this.bonusLife, this.maxLives,
                this.width, GameConstant.ITEMS_SEPARATION_LINE_HEIGHT
        );
        this.view = new GameView(this.model,this.drawManager);

        // Initialize Model
        this.model.initialize();

        this.inputDelay = new Cooldown(GameModel.INPUT_DELAY);
        this.inputDelay.reset();
    }

    /**
     * Game loop.
     */
    @Override
    public final int run() {
        super.run();

        int finalScore = this.model.calculateFinalScore();
        this.logger.info("Screen cleared with a score of " + finalScore);

        return this.returnCode;
    }

    /**
     * Main Controller Loop (Model ↔ View coordination)
     */
    @Override
    protected final void update() {
        super.update();

        // Input Handling
        if (this.inputDelay.checkFinished() && !this.model.isLevelFinished()) {

            if (!this.model.isTimerRunning()) {
                this.model.startTimer();
            }

			// Player 1 Input
			if (model.getLivesP1() > 0 && model.getShip() != null && !model.getShip().isDestroyed()) {

				boolean shift1 = inputManager.isP1ShiftDown();
				if (inputManager.isP1KeyDown(KeyEvent.VK_D)) {
					model.playerMoveOrTeleport(1, "RIGHT", shift1);
				}
				if (inputManager.isP1KeyDown(KeyEvent.VK_A)) {
					model.playerMoveOrTeleport(1, "LEFT", shift1);
				}
				if (inputManager.isP1KeyDown(KeyEvent.VK_W)) {
					model.playerMoveOrTeleport(1, "UP", shift1);
				}
				if (inputManager.isP1KeyDown(KeyEvent.VK_S)) {
					model.playerMoveOrTeleport(1, "DOWN", shift1);
				}
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_SPACE))
					model.playerFire(1);
			}

			// Player 2 Input
			if (model.getShipP2() != null && model.getLivesP2() > 0 && !model.getShipP2().isDestroyed()) {
				boolean Slash = inputManager.isP2SlashDown();

				if (inputManager.isP2KeyDown(KeyEvent.VK_RIGHT)) {
					model.playerMoveOrTeleport(2, "RIGHT", Slash);
				}

				if (inputManager.isP2KeyDown(KeyEvent.VK_LEFT)) {
					model.playerMoveOrTeleport(2, "LEFT", Slash);
				}

				if (inputManager.isP2KeyDown(KeyEvent.VK_UP)) {
					model.playerMoveOrTeleport(2, "UP", Slash);
				}

				if (inputManager.isP2KeyDown(KeyEvent.VK_DOWN)) {
					model.playerMoveOrTeleport(2, "DOWN", Slash);
				}
				if (inputManager.isP2KeyDown(java.awt.event.KeyEvent.VK_ENTER))
					model.playerFire(2);
			}

            // Update game world (Model)
            this.model.updateGameWorld();
        }

        // Timer
        if (this.model.isTimerRunning()) {
            this.model.updateElapsedTime();
        }

        // Create DTO and render via View
        HUDInfoDTO hudInfo = createHUDInfoDTO();
        this.view.render(hudInfo);

        // Game Over / Level Finish check
        if (this.model.isGameOver() && !this.model.isLevelFinished()) {
            this.model.setGameOver();
        }

        if (this.model.isLevelFinished() && this.model.getScreenFinishedCooldown().checkFinished()) {
            this.model.processLevelCompletion();
            this.isRunning = false;
        }
    }

    /**
     * Builds the DTO that passes data from Model to View.
     */
	private HUDInfoDTO createHUDInfoDTO() {

		HUDInfoDTO dto = new HUDInfoDTO(
				getWidth(),
				getHeight(),
				model.getScoreP1(),
				model.getScoreP2(),
				model.getCoin(),
				model.getLivesP1(),
				model.getLivesP2(),
				model.getLevel(),
				model.getElapsedTime(),
				model.getCurrentLevel().getLevelName(),
				model.getAchievementText(),
				model.getHealthPopupText()
		);

		dto.teleportCooldownP1 =
				(model.getShip() != null)
						? model.getShip().getTeleportCooldownProgress()
						: 1f;

		dto.teleportCooldownP2 =
				(model.getShipP2() != null)
						? model.getShipP2().getTeleportCooldownProgress()
						: 1f;

		return dto;
	}

    /**
     * Returns the game state for other systems.
     */
    public final GameState getGameState() {
        return this.model.getGameState();
    }
}
