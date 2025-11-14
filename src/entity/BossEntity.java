package entity;

import engine.DrawManager;
/**
 * Defines the contract for all boss entities that appear in the game.
 * Includes methods related to the boss's lifecycle, interaction, and movement.
 */
public interface BossEntity {

	/**
	 * Gets the current health points of the boss.
	 *
	 * @return the current health points of the boss
	 */
	int getHealPoint();

	/**
	 * Gets the score value awarded when this boss is destroyed.
	 *
	 * @return the score value of the boss
	 */
	int getPointValue();

	/**
	 * Moves the boss by the specified distances along the X and Y axes.
	 *
	 * @param distanceX distance to move along the X axis
	 * @param distanceY distance to move along the Y axis
	 */
	void move(int distanceX, int distanceY);

	/**
	 * Handles any cleanup or final logic when the boss is destroyed.
	 */
	void destroy();

	/**
	 * Checks whether the boss has been destroyed.
	 *
	 * @return {@code true} if the boss is destroyed; {@code false} otherwise
	 */
	boolean isDestroyed();

	/**
	 * Applies the given amount of damage to the boss.
	 *
	 * @param damage the amount of damage dealt to the boss
	 */
	void takeDamage(int damage);

	/**
	 * Updates the boss's state, typically used for animation or behavior changes.
	 */
	void update();

	/**
	 * Draws the boss entity on the screen.
	 *
	 * @param drawManager the {@link DrawManager} responsible for rendering the boss
	 */
	void draw(DrawManager drawManager);
}