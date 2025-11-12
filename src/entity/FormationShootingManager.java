package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import engine.Cooldown;
import engine.Core;
import engine.level.Level;

/**
 * Manages all shooting-related logic for an enemy formation.
 * This includes managing the list of available shooters,
 * handling shooting cooldowns, and re-assigning shooters
 * when one is destroyed.
 */
public class FormationShootingManager {

    /** Speed of the bullets shot. */
    private static final int BULLET_SPEED = 4;
    /** Proportion of differences between shooting times. */
    private static final double SHOOTING_VARIANCE = .2;

    /** Application logger. */
    private Logger logger;
    /** List of ships that are currently able to shoot. */
    private List<EnemyShip> shooters;
    /** The cooldown timer between shots. */
    private Cooldown shootingCooldown;

    /**
     * Constructs a new shooting manager.
     *
     * @param level      The level to get shooting frequency from.
     * @param enemyShips The complete 2D list of ships, used to find initial shooters.
     */
    public FormationShootingManager(final Level level,
                                    final List<List<EnemyShip>> enemyShips) {
        this.logger = Core.getLogger();
        this.shooters = new ArrayList<>();

        for (List<EnemyShip> column : enemyShips) {
            if (!column.isEmpty()) {
                this.shooters.add(column.get(column.size() - 1));
            }
        }

        int shootingInterval = level.getShootingFrecuency();
        int shootingVariance = (int) (shootingInterval * SHOOTING_VARIANCE);
        this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
                shootingVariance);
        this.shootingCooldown.reset();
    }

    /**
     * Attempts to fire a bullet from a random shooter.
     *
     * @param bullets The set of active bullets.
     */
    public void shoot(final Set<Bullet> bullets) {
        if (this.shooters.isEmpty()) {
            return;
        }

        int index = (int) (Math.random() * this.shooters.size());
        EnemyShip shooter = this.shooters.get(index);

        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(shooter.getPositionX()
                    + shooter.getWidth() / 2, shooter.getPositionY(), BULLET_SPEED));
        }
    }

    /**
     * Handles the destruction of a ship, re-assigning shooters if needed.
     *
     * @param destroyedShip The ship that was destroyed.
     * @param column        The column the ship belonged to.
     */
    public void onShipDestroyed(final EnemyShip destroyedShip,
                                final List<EnemyShip> column) {

        if (this.shooters.contains(destroyedShip)) {
            int destroyedShooterIndex = this.shooters.indexOf(destroyedShip);

            EnemyShip nextShooter = getNextShooter(column);

            if (nextShooter != null) {
                this.shooters.set(destroyedShooterIndex, nextShooter);
            } else {
                this.shooters.remove(destroyedShooterIndex);
                this.logger.info("Shooters list reduced to "
                        + this.shooters.size() + " members.");
            }
        }
    }

    /**
     * Finds the bottom-most, non-destroyed ship in a column
     * to designate it as the new shooter.
     *
     * @param column The column to search.
     * @return The new shooter ship, or null if the column is empty.
     */
    private EnemyShip getNextShooter(final List<EnemyShip> column) {
        Iterator<EnemyShip> iterator = column.iterator();
        EnemyShip nextShooter = null;
        while (iterator.hasNext()) {
            EnemyShip checkShip = iterator.next();
            if (checkShip != null && !checkShip.isDestroyed())
                nextShooter = checkShip;
        }
        return nextShooter;
    }
}