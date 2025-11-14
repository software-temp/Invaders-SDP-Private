package main.screen;

import java.awt.Insets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import main.engine.Cooldown;
import main.engine.Core;
import main.engine.DrawManager;
import main.engine.InputManager;

/**
 * Implements a generic main.screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Screen {

	/** Height of the interface separation line. */
	public static final int SEPARATION_LINE_HEIGHT = 45;
	/** Height of the items separation line (above items). */
	public static final int ITEMS_SEPARATION_LINE_HEIGHT = 400;
	/** Milliseconds until the main.screen accepts user input. */
	private static final int INPUT_DELAY = 1000;

	/** Draw Manager instance. */
	protected DrawManager drawManager;
	/** Input Manager instance. */
	protected InputManager inputManager;
	/** Application logger. */
	protected Logger logger;

	/** Screen width. */
	protected int width;
	/** Screen height. */
	protected int height;
	/** Frames per second shown on the main.screen. */
	protected int fps;
	/** Screen insets. */
	protected Insets insets;
	/** Time until the main.screen accepts user input. */
	protected Cooldown inputDelay;

	/** If the main.screen is running. */
	protected boolean isRunning;
	/** What kind of main.screen goes next. */
	protected int returnCode;

	/**
	 * Constructor, establishes the properties of the main.screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public Screen(final int width, final int height, final int fps) {
		this.width = width;
		this.height = height;
		this.fps = fps;

		this.drawManager = Core.getDrawManager();
		this.inputManager = Core.getInputManager();
		this.logger = Core.getLogger();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		this.returnCode = 0;
	}

	/**
	 * Initializes basic main.screen properties.
	 */
	public void initialize() {

	}

	/**
	 * Activates the main.screen.
	 * 
	 * @return Next main.screen code.
	 */
	public int run() {
		this.isRunning = true;

		while (this.isRunning) {
			long time = System.currentTimeMillis();

			update();

			time = (1000 / this.fps) - (System.currentTimeMillis() - time);
			if (time > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(time);
				} catch (InterruptedException e) {
					return 0;
				}
			}
		}

		return 0;
	}

	/**
	 * Updates the elements on main.screen and checks for events.
	 */
	protected void update() {
	}

	/**
	 * Getter for main.screen width.
	 * 
	 * @return Screen width.
	 */
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Getter for main.screen height.
	 * 
	 * @return Screen height.
	 */
	public final int getHeight() {
		return this.height;
	}
}