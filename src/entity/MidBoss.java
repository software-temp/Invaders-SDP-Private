package entity;

import engine.Core;
import entity.pattern.ApocalypseAttackPattern;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Implements a game middle boss entity.
 *
 * @author <a href="developer.ksb@gmail.com">Seungbeom Kim</a>
 *
 */
public abstract class MidBoss extends Entity implements BossEntity {

	protected int healPoint = 10;
	protected int maxHp = healPoint;
	protected int pointValue = 500;
	protected boolean isDestroyed = false;
	protected int pattern = 1;
	protected Logger logger;

	/**
	 * Constructor, establishes the boss entity's generic properties.
	 *
	 * @param positionX  Initial position of the entity in the X axis.
	 * @param positionY  Initial position of the entity in the Y axis.
	 * @param width      Width of the entity.
	 * @param height     Height of the entity.
	 * @param healPoint  HP of the entity.
	 * @param pointValue point of the entity.
	 * @param color      Color of the entity.
	 */
	public MidBoss(int positionX, int positionY, int width, int height, int healPoint, int pointValue, Color color) {
		super(positionX, positionY, width, height, color);
		this.healPoint = healPoint;
		this.maxHp = healPoint;
		this.pointValue = pointValue;
		this.logger = Core.getLogger();
	}

	@Override
	public int getHealPoint() {
		return this.healPoint;
	}

	@Override
	public int getPointValue() {
		return this.pointValue;
	}

	@Override
	public boolean isDestroyed() { return this.isDestroyed; }

    // --- [Added] Base methods for Apocalypse Pattern ---
    // Bosses that don't use this pattern (e.g., ZetaBoss) will inherit this default implementation.

    /**
     * (Used by View) Returns whether the warning is active
     * @return Always returns false for bosses without this pattern.
     */
    public boolean isApocalypseWarning() {
        return false;
    }

    /**
     * (Used by View) Returns the safe zone column index (0-9)
     * @return Returns -1 for bosses without this pattern.
     */
    public int getSafeZoneColumn() {
        return -1;
    }

    /**
     * (Used by GameModel) Returns the pattern component object itself
     * @return Returns null for bosses without this pattern.
     */
    public ApocalypseAttackPattern getApocalypsePattern() {
        return null;
    }

	@Override
	public void onCollision(Collidable other, GameModel model) {
		other.onCollideWithBoss(this, model);
	}

	@Override
	public void onHitByPlayerBullet(Bullet bullet, GameModel model) {
		model.requestBossHitByPlayerBullet(bullet, this);
	}
}
