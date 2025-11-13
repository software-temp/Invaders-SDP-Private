package main.entity;

import java.awt.Color;

import main.engine.DrawManager.SpriteType;

/**
 * Implements a generic game main.entity.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Entity {

	/** Position in the x-axis of the upper left corner of the main.entity. */
	protected int positionX;
	/** Position in the y-axis of the upper left corner of the main.entity. */
	protected int positionY;
	/** Width of the main.entity. */
	protected int width;
	/** Height of the main.entity. */
	protected int height;
	/** Color of the main.entity. */
	protected Color color;
	/** Sprite type assigned to the main.entity. */
	protected SpriteType spriteType;

	/**
	 * Constructor, establishes the main.entity's generic properties.
	 * 
	 * @param positionX
	 *            Initial position of the main.entity in the X axis.
	 * @param positionY
	 *            Initial position of the main.entity in the Y axis.
	 * @param width
	 *            Width of the main.entity.
	 * @param height
	 *            Height of the main.entity.
	 * @param color
	 *            Color of the main.entity.
	 */
	public Entity(final int positionX, final int positionY, final int width,
			final int height, final Color color) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	/**
	 * Getter for the color of the main.entity.
	 * 
	 * @return Color of the main.entity, used when drawing it.
	 */
	public final Color getColor() {
		return color;
	}

	/**
	 * Setter for the color of the main.entity.
	 *
	 * @param color
	 *            New color of the main.entity.
	 */
	public void setColor(final Color color) {
		this.color = color;
	}

	/**
	 * Getter for the X axis position of the main.entity.
	 * 
	 * @return Position of the main.entity in the X axis.
	 */
	public final int getPositionX() {
		return this.positionX;
	}

	/**
	 * Getter for the Y axis position of the main.entity.
	 * 
	 * @return Position of the main.entity in the Y axis.
	 */
	public final int getPositionY() {
		return this.positionY;
	}

	/**
	 * Setter for the X axis position of the main.entity.
	 * 
	 * @param positionX
	 *            New position of the main.entity in the X axis.
	 */
	public final void setPositionX(final int positionX) {
		this.positionX = positionX;
	}

	/**
	 * Setter for the Y axis position of the main.entity.
	 * 
	 * @param positionY
	 *            New position of the main.entity in the Y axis.
	 */
	public final void setPositionY(final int positionY) {
		this.positionY = positionY;
	}

	/**
	 * Getter for the sprite that the main.entity will be drawn as.
	 * 
	 * @return Sprite corresponding to the main.entity.
	 */
	public final SpriteType getSpriteType() {
		return this.spriteType;
	}

	/**
	 * Getter for the width of the image associated to the main.entity.
	 * 
	 * @return Width of the main.entity.
	 */
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Getter for the height of the image associated to the main.entity.
	 * 
	 * @return Height of the main.entity.
	 */
	public final int getHeight() {
		return this.height;
	}
}
