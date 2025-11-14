package main.screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import main.engine.Core;
import main.engine.Score;

/**
 * Implements the high scores main.screen, it shows player records.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class HighScoreScreen extends Screen {

	/** List of past high scores. */
	private List<Score> highScores;

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
	public HighScoreScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		this.returnCode = 1;

		try {
			this.highScores = Core.getFileManager().loadHighScores();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next main.screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on main.screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
				&& this.inputDelay.checkFinished())
			this.isRunning = false;
	}

	/**
	 * Draws the elements associated with the main.screen.
	 */
	private void draw() {
		drawManager.initDrawing(this.width, this.height);

		drawManager.getUIRenderer().drawHighScoreMenu(this.width, this.height);
		drawManager.getUIRenderer().drawHighScores(this.width, this.height, this.highScores);

		drawManager.completeDrawing();
	}
}
