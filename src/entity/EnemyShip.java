package entity;
import audio.SoundManager;


import java.awt.Color;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a enemy ship, to be destroyed by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShip extends Entity {
	
	/** Point value of a type A enemy. */
	private static final int A_TYPE_POINTS = 10;
	/** Point value of a type B enemy. */
	private static final int B_TYPE_POINTS = 20;
	/** Point value of a type C enemy. */
	private static final int C_TYPE_POINTS = 30;
	/** Point value of a bonus enemy. */
	private static final int BONUS_TYPE_POINTS = 100;

	/** Cooldown between sprite changes. */
	private Cooldown animationCooldown;
    /** Cooldown between explosions. */
    private Cooldown explosionCooldown;
	/** Checks if the ship has been hit by a bullet. */
	private boolean isDestroyed;
	/** Values of the ship, in points, when destroyed. */
	private int pointValue;

	/** Special enemy Direction enum **/
	public enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	/** Special enemy Direction variable **/
	private Direction direction;

	/** Special enemy X_SPEED variable **/
	private int X_SPEED = 0;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param spriteType
	 *            Sprite type, image corresponding to the ship.
	 */
	public EnemyShip(final int positionX, final int positionY,
			final SpriteType spriteType) {
		super(positionX, positionY, 12 * 2, 8 * 2, Color.WHITE);

		this.spriteType = spriteType;
		this.animationCooldown = Core.getCooldown(500);
        this.explosionCooldown = Core.getCooldown(500);
		this.isDestroyed = false;

		switch (this.spriteType) {
		case EnemyShipA1:
		case EnemyShipA2:
			this.pointValue = A_TYPE_POINTS;
			break;
		case EnemyShipB1:
		case EnemyShipB2:
			this.pointValue = B_TYPE_POINTS;
			break;
		case EnemyShipC1:
		case EnemyShipC2:
			this.pointValue = C_TYPE_POINTS;
			break;
		default:
			this.pointValue = 0;
			break;
		}
	}

	/**
	 * Constructor, establishes the ship's properties for a special ship, with
	 * known starting properties.
	 */
	public EnemyShip(Color color, Direction direction, int x_speed) {
		super(-32, 60, 16 * 2, 7 * 2, color);

		this.direction = direction;
		this.X_SPEED = x_speed;
		this.spriteType = SpriteType.EnemyShipSpecial;
		this.isDestroyed = false;
		this.pointValue = BONUS_TYPE_POINTS;
        this.explosionCooldown = Core.getCooldown(500);
	}

	/**
	 * Getter for the score bonus if this ship is destroyed.
	 * 
	 * @return Value of the ship.
	 */
	public final int getPointValue() {
		return this.pointValue;
	}

	/**
	 * Moves the ship the specified distance.
	 * 
	 * @param distanceX
	 *            Distance to move in the X axis.
	 * @param distanceY
	 *            Distance to move in the Y axis.
	 */
	public final void move(final int distanceX, final int distanceY) {
		this.positionX += distanceX;
		this.positionY += distanceY;
	}

	/**
	 * Updates attributes, mainly used for animation purposes.
	 */
	public final void update() {
		if (this.animationCooldown.checkFinished()) {
			this.animationCooldown.reset();

			switch (this.spriteType) {
			case EnemyShipA1:
				this.spriteType = SpriteType.EnemyShipA2;
				break;
			case EnemyShipA2:
				this.spriteType = SpriteType.EnemyShipA1;
				break;
			case EnemyShipB1:
				this.spriteType = SpriteType.EnemyShipB2;
				break;
			case EnemyShipB2:
				this.spriteType = SpriteType.EnemyShipB1;
				break;
			case EnemyShipC1:
				this.spriteType = SpriteType.EnemyShipC2;
				break;
			case EnemyShipC2:
				this.spriteType = SpriteType.EnemyShipC1;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Destroys the ship, causing an explosion.
	 */
	public final void destroy() {
        if (!this.isDestroyed) {
            this.isDestroyed = true;
            this.spriteType = SpriteType.Explosion;
			SoundManager.stop("sfx/disappearance.wav");
            SoundManager.play("sfx/disappearance.wav");
            this.explosionCooldown.reset();
        }
	}

	/**
	 * Checks if the ship has been destroyed.
	 * 
	 * @return True if the ship has been destroyed.
	 */
	public final boolean isDestroyed() {
		return this.isDestroyed;
	}

	public final Direction getDirection() {
		return this.direction;
	}

	public final void setDirection(final Direction direction) {
		this.direction = direction;
	}

	public final int getXSpeed() {
		return this.X_SPEED;
	}

	public final void setXSpeed(final int x_speed) {
		this.X_SPEED = x_speed;
	}

    /**
     * Check if the explosion effect is finished.
     * @return True if the explosion is finished.
     */
    public final boolean isExplosionFinished() {
        return this.isDestroyed && this.explosionCooldown.checkFinished();
    }

	public final String getEnemyType() {

		switch (this.spriteType) {
			case EnemyShipA1:
			case EnemyShipA2:
				return "enemyA";
			case EnemyShipB1:
			case EnemyShipB2:
				return "enemyB";
			case EnemyShipC1:
			case EnemyShipC2:
				return "enemyC";
			default:
				return null;
		}
	}
}
