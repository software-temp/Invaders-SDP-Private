package engine;

import audio.SoundManager;

import java.awt.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.level.LevelManager;
import entity.GameConstant;
import screen.*;
import test.TestScreen;
import entity.GameConstant;
/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	/** Width of current screen. (excepting white space) */
	public static int FRAME_WIDTH;
	/** Height of current screen. (excepting white space) */
	public static int FRAME_HEIGHT;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;

	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Level manager for loading level settings. */
	private static LevelManager levelManager;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;

	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}
        int FRAME_HEIGHT_TOTAL, FRAME_WIDTH_TOTAL;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        FRAME_WIDTH_TOTAL = (int) (screenSize.getWidth() * 0.8);
        FRAME_HEIGHT_TOTAL = (int) (screenSize.getHeight() * 0.9);

//      screen size ratio
        double scaleX = (double) FRAME_WIDTH_TOTAL / 1228.0;
        double scaleY = (double) FRAME_HEIGHT_TOTAL / 777.0;

        DrawManager.getInstance().setScale(scaleX, scaleY);

		frame = new Frame(FRAME_WIDTH_TOTAL, FRAME_HEIGHT_TOTAL);
		DrawManager.getInstance().setFrame(frame);
		FRAME_WIDTH = frame.getWidth();
		FRAME_HEIGHT = frame.getHeight();
        GameConstant.initialize(FRAME_WIDTH, FRAME_HEIGHT);

		levelManager = new LevelManager();
		GameState gameState = new GameState(4, 0, MAX_LIVES, MAX_LIVES, 0, 0,0);

		if (GameConstant.isTest){
			while (true) {
				currentScreen = new TestScreen(FRAME_WIDTH, FRAME_HEIGHT);
				frame.setScreen(currentScreen);
			}
		}
        int returnCode = 1;
		do {
            gameState = new GameState(4, 0, MAX_LIVES,MAX_LIVES, 0, 0,gameState.getCoin());
			switch (returnCode) {
                case 1:
                    // Main menu.
                    currentScreen = new TitleScreen(FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS);
                    if (!SoundManager.isCurrentLoop("sfx/menu_music.wav")) {
                        SoundManager.playLoop("sfx/menu_music.wav");
                    }
                    LOGGER.info("Starting " + Core.FRAME_WIDTH + "x" + Core.FRAME_HEIGHT
                            + " title screen at " + GameConstant.FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing title screen.");
                    break;
                case 2:
                    do {
                        // One extra life every few levels
                        boolean bonusLife = gameState.getLevel()
                                % EXTRA_LIFE_FRECUENCY == 0
                                && gameState.getLivesRemaining() < MAX_LIVES;

						// Music for each level
						SoundManager.stopAll();
						SoundManager.playLoop("sfx/level" + gameState.getLevel() + ".wav");

                        engine.level.Level currentLevel = levelManager.getLevel(gameState.getLevel());

                        // TODO: Handle case where level is not found after JSON loading is implemented.
                        if (currentLevel == null) {
                          // For now, we can just break or default to level 1 if we run out of levels.
                          // This will be important when the number of levels is defined by maps.json
                          break;
                        }

						SoundManager.stopAll();
						SoundManager.playLoop("sfx/level" + gameState.getLevel() + ".wav");

                        // Start a new level
                        currentScreen = new GameScreen(
                                gameState,
                                currentLevel,
                                bonusLife,
                                MAX_LIVES,
                                FRAME_WIDTH,
                                FRAME_HEIGHT,
                                GameConstant.FPS
                        );

                        LOGGER.info("Starting " + Core.FRAME_WIDTH + "x" + Core.FRAME_HEIGHT
                                + " game screen at " + GameConstant.FPS + " fps.");
                        frame.setScreen(currentScreen);
                        LOGGER.info("Closing game screen.");
                        gameState = ((GameScreen) currentScreen).getGameState();
                        if (gameState.getLivesRemaining() > 0 || gameState.getLivesRemainingP2() > 0) {
							SoundManager.stopAll();
							SoundManager.play("sfx/levelup.wav");

							LOGGER.info("Opening shop screen with "
                                    + gameState.getCoin() + " coins.");

                            //Launch the ShopScreen (between levels)
                            currentScreen = new ShopScreen(gameState, FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS, true);

                            frame.setScreen(currentScreen);
                            LOGGER.info("Closing shop screen.");

                            gameState = new GameState(
                                    gameState.getLevel() + 1,          // Increment level
                                    gameState.getScore(),              // Keep current score
                                    gameState.getLivesRemaining(),     // Keep remaining lives
									gameState.getLivesRemainingP2(),   // Keep remaining livesP2
                                    gameState.getBulletsShot(),        // Keep bullets fired
                                    gameState.getShipsDestroyed(),     // Keep ships destroyed
                                    gameState.getCoin()                // Keep current coins
                            );
                        }
                        // Loop while player still has lives and levels remaining
                    } while (gameState.getLivesRemaining() > 0 || gameState.getLivesRemainingP2() > 0);

					SoundManager.stopAll();
					SoundManager.play("sfx/gameover.wav");

                    LOGGER.info("Starting " + Core.FRAME_WIDTH + "x" + Core.FRAME_HEIGHT
                            + " score screen at " + GameConstant.FPS + " fps, with a score of "
                            + gameState.getScore() + ", "
                            + gameState.getLivesRemaining() + " lives remaining, "
                            + gameState.getBulletsShot() + " bullets shot and "
                            + gameState.getShipsDestroyed() + " ships destroyed.");

                    currentScreen = new ScoreScreen(FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS, gameState);
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing score screen.");
                    break;
                case 3:
                    // High scores
                    currentScreen = new HighScoreScreen(FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS);
                    LOGGER.info("Starting " + Core.FRAME_WIDTH + "x" + Core.FRAME_HEIGHT
                            + " high score screen at " + GameConstant.FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing high score screen.");
                    break;
                case 4:
                    // Shop opened manually from main menu

                    currentScreen = new ShopScreen(gameState, FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS, false);
                    LOGGER.info("Starting shop screen (menu) with " + gameState.getCoin() + " coins.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing shop screen (menu).");
                    break;
                case 6:
                    // Achievements
                    currentScreen = new AchievementScreen(FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS);
                    LOGGER.info("Starting " + Core.FRAME_WIDTH + "x" + Core.FRAME_HEIGHT
                            + " achievement screen at " + GameConstant.FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing achievement screen.");
                    break;
				case 8: // (추가) CreditScreen
					currentScreen = new CreditScreen(FRAME_WIDTH, FRAME_HEIGHT, GameConstant.FPS);
					LOGGER.info("Starting " + currentScreen.getClass().getSimpleName() + " screen.");
					returnCode = frame.setScreen(currentScreen);
					break;
                default:
                    break;
            }

        } while (returnCode != 0);

        fileHandler.flush();
        fileHandler.close();
        System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}