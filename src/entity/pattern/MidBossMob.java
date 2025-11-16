package entity.pattern;

import engine.DrawManager;
import entity.MidBoss;

import java.awt.*;

/**
 * Represents a specialized mob or child ship spawned by a MidBoss,
 * inheriting base characteristics from MidBoss.
 */
public class MidBossMob extends MidBoss {
    /** The width of the child mob entity. */
    private static final int WIDTH = 24;
    /** The height of the child mob entity. */
    private static final int HEIGHT= 16;
    /** Flag to determine horizontal movement direction. True for right, false for left. */
    private boolean directionRight = true;
    /**
     * Constructs a new MidBossMob entity.
     * @param INIT_POS_X Initial X position.
     * @param INIT_POS_Y Initial Y position.
     * @param healthPoint Initial health points.
     * @param pointValue Score value granted upon destruction.
     * @param color The drawing color of the entity.
     */
    public MidBossMob(int INIT_POS_X, int INIT_POS_Y, int healthPoint, int pointValue, Color color) {
        super(INIT_POS_X, INIT_POS_Y, WIDTH, HEIGHT, healthPoint, pointValue, color);
        this.spriteType = DrawManager.SpriteType.EnemyShipB1;
    }

    /**
     * Moves the entity by the specified distances in the X and Y axes.
     * @param distanceX Distance to move along the X-axis.
     * @param distanceY Distance to move along the Y-axis.
     */
    @Override
    public void move(int distanceX, int distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    @Override
    public void update() {
        // NOTE: Movement logic is typically delegated to the movement strategy in this design.
    }
    /**
     * Reduces health and destroys the entity if it drops to zero or below.
     *
     * @param damage The amount of damage to inflict.
     */
    @Override
    public void takeDamage(int damage) {
        this.healPoint -= damage;
    }

    public void destroy() {
        this.isDestroyed = true;
        this.spriteType = DrawManager.SpriteType.Explosion;
        this.logger.info("Mob Destroyed");
    }

    @Override
    public int getHealPoint() { return this.healPoint; }

    @Override
    public int getPointValue() { return this.pointValue; }

    @Override
    public boolean isDestroyed() { return this.isDestroyed; }

    public int getMaxHp() { return this.maxHp; }

    public boolean getDirectionRight() { return this.directionRight; }

    public void setDirectionRight(boolean directionRightValue) { this.directionRight = directionRightValue; }

}
