package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Bullet extends Entity {
    // === [ADD] Owner flag: 1 = P1, 2 = P2, null for legacy compatibility ===
    private Integer ownerId;

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }


    /**
	 * Speed of the bullet, positive or negative depending on direction -
	 * positive is down.
	 */
	private int speed;

	/** number of Penetrations */
	private int penetrationCount;
	/** Number of possible penetrations */
	private int maxPenetration;


	/**
	 * Constructor, establishes the bullet's properties.
	 * 
	 * @param positionX
	 *            Initial position of the bullet in the X axis.
	 * @param positionY
	 *            Initial position of the bullet in the Y axis.
	 * @param speed
	 *            Speed of the bullet, positive or negative depending on
	 *            direction - positive is down.
	 */
	public Bullet(final int positionX, final int positionY, final int speed) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

		this.speed = speed;
		this.penetrationCount = 0;
		this.maxPenetration = ShopItem.getPenetrationCount();

		setSprite();
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		if (speed < 0)
			this.spriteType = SpriteType.Bullet;
		else
			this.spriteType = SpriteType.EnemyBullet;
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {
		this.positionY += this.speed;
	}

	/**
	 * Setter of the speed of the bullet.
	 * 
	 * @param speed
	 *            New speed of the bullet.
	 */
	public final void setSpeed(final int speed) {
		this.speed = speed;
	}

	/**
	 * Getter for the speed of the bullet.
	 * 
	 * @return Speed of the bullet.
	 */
	public final int getSpeed() {
		return this.speed;
	}

	/**
	 * getter Bullet persistence status
	 * @return If true the bullet persists, If false it is deleted.
	 */
	public final boolean penetration() {
		this.penetrationCount++;

		return this.penetrationCount <= this.maxPenetration;
	}

	/**
	 *Check for penetration possibility
	 * @return True, Penetrable
	 */
	public final boolean canPenetration(){
		return this.penetrationCount < this.maxPenetration;
	}

	/**
	 * reset penetration setting
	 */
	public final void resetPenetration() {
		this.penetrationCount = 0;
		this.maxPenetration = ShopItem.getPenetrationCount();
	}

}
