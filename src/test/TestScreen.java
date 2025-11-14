package test;

import main.engine.Core;
import main.engine.DTO.HUDInfoDTO;
import main.entity.GameModel;
import main.screen.Screen;

public class TestScreen extends Screen {

	private TestModel model;
	private TestView view;
	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width  Screen width.
	 * @param height Screen height.
	 * @param fps    Frames per second, frame rate at which the game is run.
	 */
	public TestScreen(int width, int height, int fps) {
		super(width, height, fps);
	}

	public final void initialize(){
		super.initialize();

		this.model = new TestModel(this.width, ITEMS_SEPARATION_LINE_HEIGHT);
		this.view = new TestView(this.model, this.drawManager, this.width, this.height);

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
		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 * This is the main Controller loop.
	 */
	protected final void update() {
		super.update();

		// Check if input delay is over and game is not finished
		if (this.inputDelay.checkFinished()) {

			// Process initializing position if esc key is pressed
			if (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_ESCAPE)) {
				this.isRunning = false;
			}

			// 1. (Controller) Process user input and tell Model to update
			// Player 1 Input
			// (Get player object from model to check status)
			if (model.playerAvailable()) {
				// (Controller detects input and sends a 'command' to the model)
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_D))
					model.playerMove("RIGHT");
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_A))
					model.playerMove("LEFT");
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_W))
					model.playerMove("UP");
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_S))
					model.playerMove("DOWN");
				if (inputManager.isP1KeyDown(java.awt.event.KeyEvent.VK_SPACE))
					model.playerFire();
			}

			// 2. (Controller) Tell Model to update all game logic
			this.model.update();
		}

		// 3. (Controller) Tell View to draw the current Model state
		this.view.render(createHUDInfoDTO());
	}
	/**
	 * Builds the DTO that passes data from Model to View.
	 */
	private HUDInfoDTO createHUDInfoDTO() {
		return new HUDInfoDTO(
				getWidth(),
				getHeight(),
				0,
				0,
				0,
				0,
				0,
				0,
				0,
				"test",
				"test",
				"test"
		);
	}
}
