package entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.awt.Color;

import engine.Core;
import engine.level.Level;

/**
 * Groups enemy ships into a formation.
 * (MODEL - Contains data and coordinates logic using components)
 */
public class EnemyShipFormationModel implements Iterable<EnemyShip> {

    /** Initial position in the x-axis. */
    private static final int INIT_POS_X = 20;
    /** Initial position in the y-axis. */
    private static final int INIT_POS_Y = 100;
    /** Distance between ships. */
    private static final int SEPARATION_DISTANCE = 40;
    /** Minimum speed allowed. */
    private static final int MINIMUM_SPEED = 10;

    /** Application logger. */
    private Logger logger;
    /** Level reference. */
    private Level levelObj;

    /** List of enemy ships forming the formation. */
    private List<List<EnemyShip>> enemyShips;

    /** Number of ships in the formation - horizontally. */
    private int nShipsWide;
    /** Number of ships in the formation - vertically. */
    private int nShipsHigh;
    /** Initial ship speed. */
    private int baseSpeed;
    /** Speed of the ships. */
    private int movementSpeed;
    /** Interval between movements, in frames. */
    private int movementInterval;
    /** Total width of the formation. */
    private int width;
    /** Total height of the formation. */
    private int height;
    /** Position in the x-axis of the upper left corner of the formation. */
    private int positionX;
    /** Position in the y-axis of the upper left corner of the formation. */
    private int positionY;
    /** Width of one ship. */
    private int shipWidth;
    /** Height of one ship. */
    private int shipHeight;
    /** Number of not destroyed ships. */
    private int shipCount;

    /** The logic component responsible for movement. */
    private EnemyShipFormationMovement movementStrategy;
    /** The logic component responsible for shooting. */
    private FormationShootingManager shootingManager;

    /**
     * Constructor
     *
     * @param level The level data (for speed, shooting, etc.).
     */
    public EnemyShipFormationModel(final Level level, int screenWidth) {
        this.logger = Core.getLogger();
        this.movementInterval = 0;
        this.levelObj = level;

        EnemyShipFactory builder = new EnemyShipFactory();
        this.enemyShips = builder.build(
                level,
                level.getFormationWidth(),
                level.getFormationHeight()
        );

        this.movementStrategy = new EnemyShipFormationMovement(this,GameConstant.ITEMS_SEPARATION_LINE_HEIGHT,screenWidth);
        this.shootingManager = new FormationShootingManager(level, this.enemyShips);

        this.nShipsWide = level.getFormationWidth();
        this.nShipsHigh = level.getFormationHeight();
        this.baseSpeed = level.getBaseSpeed();
        this.movementSpeed = this.baseSpeed;

        this.positionX = INIT_POS_X;
        this.positionY = INIT_POS_Y;
        this.shipCount = 0;
        for (List<EnemyShip> column : this.enemyShips) {
            this.shipCount += column.size();
        }

        this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
                + " ship formation in (" + positionX + "," + positionY + ")");

        if (!this.enemyShips.isEmpty() && !this.enemyShips.get(0).isEmpty()) {
            this.shipWidth = this.enemyShips.get(0).get(0).getWidth();
            this.shipHeight = this.enemyShips.get(0).get(0).getHeight();
        } else {
            this.shipWidth = 0;
            this.shipHeight = 0;
            this.logger.warning("EnemyShipFormationModel: No ships were created.");
        }

        this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE + this.shipWidth;
        this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE + this.shipHeight;
    }

    /**
     * Returns the 2D list of enemy ships for the View to draw.
     * @return 2D list of enemy ships.
     */
    public final List<List<EnemyShip>> getEnemyShips() {
        return this.enemyShips;
    }

    /**
     * Updates the position of the ships.
     */
    public final void update() {
        cleanUp();

        double remainingProportion = (double) this.shipCount
                / (this.nShipsHigh * this.nShipsWide);
        this.movementSpeed = (int) (Math.pow(remainingProportion, 2)
                * this.baseSpeed);
        this.movementSpeed += MINIMUM_SPEED;

        movementInterval++;
        if (movementInterval >= this.movementSpeed) {
            movementInterval = 0;

            this.movementStrategy.updateMovement();

            List<EnemyShip> destroyed;
            for (List<EnemyShip> column : this.enemyShips) {
                destroyed = new ArrayList<EnemyShip>();
                for (EnemyShip ship : column) {
                    if (ship != null && ship.isExplosionFinished()) {
                        destroyed.add(ship);
                        this.logger.info("Removed enemy "
                                + column.indexOf(ship) + " from column "
                                + this.enemyShips.indexOf(column));
                    }
                }

                column.removeAll(destroyed);
            }

            for (List<EnemyShip> column : this.enemyShips)
                for (EnemyShip enemyShip : column) {
                    enemyShip.update();
                }
        }
    }

    /**
     * Cleans empty columns, adjusts the width and height of the formation.
     */
    private void cleanUp() {
        for (int i = this.enemyShips.size() - 1; i >= 0; i--) {
            if (this.enemyShips.get(i).isEmpty()) {
                this.enemyShips.remove(i);
                logger.info("Removed column " + i);
            }
        }

        if (this.enemyShips.isEmpty()) {
            this.width = 0;
            this.height = 0;
            return;
        }

        int minPositionY = Integer.MAX_VALUE;
        int maxPositionY = Integer.MIN_VALUE;
        int leftMostPoint = Integer.MAX_VALUE;
        int rightMostPoint = Integer.MIN_VALUE;
        for (List<EnemyShip> column : this.enemyShips) {
            minPositionY = Math.min(minPositionY, column.get(0).getPositionY());
            maxPositionY = Math.max(maxPositionY, column.get(column.size() - 1).getPositionY());
            int columnX = column.get(0).getPositionX();
            leftMostPoint = Math.min(leftMostPoint, columnX);
            rightMostPoint = Math.max(rightMostPoint, columnX);
        }
        this.width = rightMostPoint - leftMostPoint + this.shipWidth;
        this.height = maxPositionY - minPositionY + this.shipHeight;
        this.positionX = leftMostPoint;
        this.positionY = minPositionY;
    }

    /**
     * Shoots a bullet downwards.
     *
     * @param bullets
     * Bullets set to add the bullet being shot.
     */
    public final void shoot(final Set<Bullet> bullets) {
        this.shootingManager.shoot(bullets);
    }

    /**
     * Destroys a ship.
     *
     * @param destroyedShip
     * Ship to be destroyed.
     */
    public final void destroy(final EnemyShip destroyedShip) {

        for (int i = 0; i < this.enemyShips.size(); i++) {
            List<EnemyShip> column = this.enemyShips.get(i);
            int shipIndexInColumn = column.indexOf(destroyedShip);

            if (shipIndexInColumn != -1) {
                if (!column.get(shipIndexInColumn).isDestroyed()) {
                    column.get(shipIndexInColumn).destroy(false);
                    this.logger.info("Destroyed ship in (" + i + "," + shipIndexInColumn + ")");
                    this.shipCount--;

                    this.shootingManager.onShipDestroyed(destroyedShip, column);
                }
                return;
            }
        }
    }

    /**
     * Returns an iterator over the ships in the formation.
     */
    @Override
    public final Iterator<EnemyShip> iterator() {
        return new Iterator<EnemyShip>() {
            private int columnIterator = 0;
            private int rowIterator = 0;

            @Override
            public boolean hasNext() {
                while (columnIterator < enemyShips.size()) {
                    if (rowIterator < enemyShips.get(columnIterator).size()) {
                        return true;
                    }
                    columnIterator++;
                    rowIterator = 0;
                }
                return false;
            }

            @Override
            public EnemyShip next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                return enemyShips.get(columnIterator).get(rowIterator++);
            }
        };
    }

    /**
     * Destroy all ships in the formation.
     *
     * @return The number of destroyed ships.
     */
    public final int destroyAll() {
        int destroyed = 0;
        for (List<EnemyShip> column : this.enemyShips) {
            for (EnemyShip enemyShip : column) {
                if (!enemyShip.isDestroyed()) {
                    enemyShip.destroy(false);
                    destroyed++;
                    this.shootingManager.onShipDestroyed(enemyShip, column);
                }
            }
        }
        this.shipCount = 0;
        return destroyed;
    }

    /**
     * Checks if there are any ships remaining.
     *
     * @return True when all ships have been destroyed.
     */
    public final boolean isEmpty() {
        return this.shipCount <= 0;
    }

    /**
     * Activates slowdown effect on the formation.
     */
    public void activateSlowdown() {
        this.movementStrategy.activateSlowdown();
    }

    /**
     * Clears all ships from the formation and resets the shooting manager.
     */
    public final void clear() {
        for (List<EnemyShip> column : this.enemyShips) {
            column.clear();
        }
        this.enemyShips.clear();
        this.shipCount = 0;

        this.shootingManager = new FormationShootingManager(this.levelObj, this.enemyShips);
    }

    /**
     * Applies a specific color to all ships in the formation.
     * @param color The color to apply.
     */
    public void applyEnemyColor(final Color color) {
        for (java.util.List<EnemyShip> column : this.getEnemyShips()) {
            for (EnemyShip ship : column) {
                if (ship != null && !ship.isDestroyed()) {
                    ship.setColor(color);
                }
            }
        }
    }

    /**
     * Gets the current X position of the formation's top-left corner.
     * @return X position.
     */
    public int getPositionX() { return this.positionX; }

    /**
     * Gets the current Y position of the formation's top-left corner.
     * @return Y position.
     */
    public int getPositionY() { return this.positionY; }

    /**
     * Gets the calculated width of the entire formation.
     * @return Formation width.
     */
    public int getWidth() { return this.width; }

    /**
     * Gets the calculated height of the entire formation.
     * @return Formation height.
     */
    public int getHeight() { return this.height; }

    /**
     * Sets the new position of the formation's top-left corner.
     * @param x New X position.
     * @param y New Y position.
     */
    public void setPosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Applies movement deltas to all individual ships in the formation.
     * @param movementX Pixels to move horizontally.
     * @param movementY Pixels to move vertically.
     */
    public void moveAllShips(int movementX, int movementY) {
        for (List<EnemyShip> column : this.enemyShips)
            for (EnemyShip enemyShip : column) {
                enemyShip.move(movementX, movementY,false);
            }
    }
}