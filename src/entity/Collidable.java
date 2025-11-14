package entity;

public interface Collidable {
	
	/**
	 * Defines the action to be taken when this object collides with another.
	 *
	 * @param other The {@code Collidable} object this object has collided with.
	 */
	void onCollision(Collidable other);
}
