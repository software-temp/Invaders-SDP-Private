package entity;

public interface Collidable {

	/**
	 * Returns this object as an Entity.
	 * Used for bounding-box collision checks.
	 */
	Entity asEntity();

	/**
	 * Defines how this object should react when a collision occurs.
	 * Each entity implements its own collision behavior.
	 */
	void onCollision(Collidable other, GameModel gameModel);
}
