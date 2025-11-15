package entity;

public interface HasBounds {

	/**
	 * Getter for the X axis position of the entity.
	 *
	 * @return Position of the entity in the X axis.
	 */
	int getPositionX();

	/**
	 * Getter for the Y axis position of the entity.
	 *
	 * @return Position of the entity in the Y axis.
	 */
	int getPositionY();

	/**
	 * Getter for the width of the image associated to the entity.
	 *
	 * @return Width of the entity.
	 */
	int getWidth();

	/**
	 * Getter for the height of the image associated to the entity.
	 *
	 * @return Height of the entity.
	 */
	int getHeight();
}
