package engine;

public interface IController {

	/**
	 * Processes user input and updates the game state using Model's inner logics.
	 *
	 * @param deltaTime The time elapsed since the last update, in seconds.
	 */
	void update(float deltaTime);
	/** Render view */
	void render();

}
