package entity;

public interface IModel {
	/**
	 * Updates the state of the model based on game logic.
	 *
	 * @param deltaTime The time elapsed since the last update, in seconds.
	 */
	void update(float deltaTime);
}
