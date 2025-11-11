package entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.awt.Color;

import screen.Screen;
import screen.GameScreen;
import engine.Cooldown;
import engine.Core;
import engine.level.Level;

/**
 * Groups enemy ships into a formation that moves together.
 * (MODEL - Contains all data and logic)
 */
public class EnemyShipFormationModel implements Iterable<EnemyShip> {

    /** Initial position in the x-axis. */
    private static final int INIT_POS_X = 20;
    /** Initial position in the y-axis. */
    private static final int INIT_POS_Y = 100;
    /** Distance between ships. */
    private static final int SEPARATION_DISTANCE = 40;
    /** Downwards speed of the formation. */
    private static final int Y_SPEED = 4;
    /** Speed of the bullets shot by the members. */
    private static final int BULLET_SPEED = 4;
    /** Proportion of differences between shooting times. */
    private static final double SHOOTING_VARIANCE = .2;
    /** Margin on the sides of the screen. */
    private static final int SIDE_MARGIN = 20;
    /** Margin on the bottom of the screen. */
    private static final int BOTTOM_MARGIN = 80;
    /** Distance to go down each pass. */
    private static final int DESCENT_DISTANCE = 20;
    /** Minimum speed allowed. */
    private static final int MINIMUM_SPEED = 10;

    /** Application logger. */
    private Logger logger;
    /** Level reference to read enemyTypes/counts. */
    private Level levelObj;

    /** List of enemy ships forming the formation. */
    private List<List<EnemyShip>> enemyShips;
    /** Minimum time between shots. */
    private Cooldown shootingCooldown;
    /** Number of ships in the formation - horizontally. */
    private int nShipsWide;
    /** Number of ships in the formation - vertically. */
    private int nShipsHigh;
    /** Time between shots. */
    private int shootingInterval;
    /** Variance in the time between shots. */
    private int shootingVariance;
    /** Initial ship speed. */
    private int baseSpeed;
    /** Speed of the ships. */
    private int movementSpeed;
    /** Current direction the formation is moving on. */
    private Direction currentDirection;
    /** Direction the formation was moving previously. */
    private Direction previousDirection;
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
    /** List of ships that are able to shoot. */
    private List<EnemyShip> shooters;
    /** Number of not destroyed ships. */
    private int shipCount;
    /** Number of slowdown movement */
    private int slowDownCount;
    /** Flag to check if slowdown is active */
    private boolean isSlowedDown;
    /** Original X_SPEED value */
    private static final int ORIGINAL_X_SPEED = 8;
    /** Slowed down X_SPEED value */
    private static final int SLOWED_X_SPEED = 4;
    /** Duration of slowdown effect (in movement cycles) */
    private static final int SLOWDOWN_DURATION = 18;
    /** Screen to draw ships on. */
    private Screen screen;

    /** Directions the formation can move. */
    private enum Direction {
        /** Movement to the right-down diagonal. */
        DOWN_RIGHT,
        /** Movement to the left-down diagonal. */
        DOWN_LEFT,
        /** Movement to the right-up diagonal. */
        UP_RIGHT,
        /** Movement to the left-up diagonal. */
        UP_LEFT
    };

    /**
     * Constructor (Refactored to accept pre-built ships)
     *
     * @param level The level data (for speed, shooting, etc.).
     */
    public EnemyShipFormationModel(final Level level) {
        this.logger = Core.getLogger();
        this.currentDirection = Direction.DOWN_RIGHT;
        this.movementInterval = 0;

        FormationBuilder builder = new FormationBuilder();
        List<List<EnemyShip>> builtShips = builder.build(
                level,
                level.getFormationWidth(),
                level.getFormationHeight()
        );
        // Read values directly from Level
        this.nShipsWide = level.getFormationWidth();
        this.nShipsHigh = level.getFormationHeight();
        this.shootingInterval = level.getShootingFrecuency();
        this.shootingVariance = (int) (level.getShootingFrecuency() * SHOOTING_VARIANCE);
        this.baseSpeed = level.getBaseSpeed();
        this.movementSpeed = this.baseSpeed;
        this.levelObj = level;
        this.shooters = new ArrayList<EnemyShip>();

        // Receive the pre-built list of ships
        this.enemyShips = builtShips;

        // Set initial position and count ships
        this.positionX = INIT_POS_X;
        this.positionY = INIT_POS_Y;
        this.shipCount = 0;
        for (List<EnemyShip> column : this.enemyShips) {
            this.shipCount += column.size();
        }

        this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
                + " ship formation in (" + positionX + "," + positionY + ")");

        // Calculate formation width/height and assign shooters
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

        for (List<EnemyShip> column : this.enemyShips) {
            if (!column.isEmpty()) {
                this.shooters.add(column.get(column.size() - 1));
            }
        }
    }

    /**
     * Returns the 2D list of enemy ships for the View to draw.
     * @return 2D list of enemy ships.
     */
    public final List<List<EnemyShip>> getEnemyShips() {
        return this.enemyShips;
    }

    /**
     * Associates the formation to a given screen.
     *
     * @param newScreen
     * Screen to attach.
     */
    public final void attach(final Screen newScreen) {
        this.screen = newScreen;
    }
    /**
     * Updates the position of the ships.
     */
    public final void update() {
        if(this.shootingCooldown == null) {
            this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
                    shootingVariance);
            this.shootingCooldown.reset();
        }

        cleanUp();

        int movementX = 0;
        int movementY = 0;
        double remainingProportion = (double) this.shipCount
                / (this.nShipsHigh * this.nShipsWide);
        this.movementSpeed = (int) (Math.pow(remainingProportion, 2)
                * this.baseSpeed);
        this.movementSpeed += MINIMUM_SPEED;

        movementInterval++;
        if (movementInterval >= this.movementSpeed) {
            movementInterval = 0;
            updateSlowdown();

            boolean isAtBottom = positionY + this.height > GameScreen.getItemsSeparationLineHeight();
            boolean isAtRightSide = positionX + this.width >= screen.getWidth() - SIDE_MARGIN;
            boolean isAtLeftSide = positionX <= SIDE_MARGIN;
            boolean isAtTop = positionY <= INIT_POS_Y;

            // Diagonal movement direction change logic
            if (currentDirection == Direction.DOWN_RIGHT) {
                if (isAtBottom && isAtRightSide) {
                    currentDirection = Direction.UP_LEFT;
                    this.logger.info("Formation now moving up-left (hit corner)");
                } else if (isAtBottom) {
                    currentDirection = Direction.UP_RIGHT;
                    this.logger.info("Formation now moving up-right (hit bottom)");
                } else if (isAtRightSide) {
                    currentDirection = Direction.DOWN_LEFT;
                    this.logger.info("Formation now moving down-left (hit right wall)");
                }
            } else if (currentDirection == Direction.DOWN_LEFT) {
                if (isAtBottom && isAtLeftSide) {
                    currentDirection = Direction.UP_RIGHT;
                    this.logger.info("Formation now moving up-right (hit corner)");
                } else if (isAtBottom) {
                    currentDirection = Direction.UP_LEFT;
                    this.logger.info("Formation now moving up-left (hit bottom)");
                } else if (isAtLeftSide) {
                    currentDirection = Direction.DOWN_RIGHT;
                    this.logger.info("Formation now moving down-right (hit left wall)");
                }
            } else if (currentDirection == Direction.UP_RIGHT) {
                if (isAtTop && isAtRightSide) {
                    currentDirection = Direction.DOWN_LEFT;
                    this.logger.info("Formation now moving down-left (hit corner)");
                } else if (isAtTop) {
                    currentDirection = Direction.DOWN_RIGHT;
                    this.logger.info("Formation now moving down-right (back to top)");
                } else if (isAtRightSide) {
                    currentDirection = Direction.UP_LEFT;
                    this.logger.info("Formation now moving up-left (hit right wall)");
                }
            } else if (currentDirection == Direction.UP_LEFT) {
                if (isAtTop && isAtLeftSide) {
                    currentDirection = Direction.DOWN_RIGHT;
                    this.logger.info("Formation now moving down-right (hit corner)");
                } else if (isAtTop) {
                    currentDirection = Direction.DOWN_LEFT;
                    this.logger.info("Formation now moving down-left (back to top)");
                } else if (isAtLeftSide) {
                    currentDirection = Direction.UP_RIGHT;
                    this.logger.info("Formation now moving up-right (hit left wall)");
                }
            }

            int currentXSpeed = getCurrentXSpeed();
            if (currentDirection == Direction.DOWN_RIGHT) {
                movementX = currentXSpeed;   // right
                movementY = Y_SPEED;   // down
            } else if (currentDirection == Direction.DOWN_LEFT) {
                movementX = -currentXSpeed;  // left
                movementY = Y_SPEED;   // down
            } else if (currentDirection == Direction.UP_RIGHT) {
                movementX = currentXSpeed;   // right
                movementY = -Y_SPEED;  // up
            } else if (currentDirection == Direction.UP_LEFT) {
                movementX = -currentXSpeed;  // left
                movementY = -Y_SPEED;  // up
            }

            positionX += movementX;
            positionY += movementY;

            // Cleans explosions.
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
                    enemyShip.move(movementX, movementY);
                    enemyShip.update();
                }
        }
    }

    /**
     * Cleans empty columns, adjusts the width and height of the formation.
     */
    private void cleanUp() {
        // Iterate backwards to safely remove elements from the list.
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

        int maxColumn = 0;
        int minPositionY = Integer.MAX_VALUE;
        int leftMostPoint = Integer.MAX_VALUE;
        int rightMostPoint = Integer.MIN_VALUE;

        for (List<EnemyShip> column : this.enemyShips) {
            // Height of this column
            int columnSize = column.get(column.size() - 1).getPositionY()
                    - this.positionY + this.shipHeight;
            maxColumn = Math.max(maxColumn, columnSize);
            minPositionY = Math.min(minPositionY, column.get(0).getPositionY());

            int columnX = column.get(0).getPositionX();
            leftMostPoint = Math.min(leftMostPoint, columnX);
            rightMostPoint = Math.max(rightMostPoint, columnX);
        }

        this.width = rightMostPoint - leftMostPoint + this.shipWidth;
        this.height = maxColumn;

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
        // For now, only ships in the bottom row are able to shoot.
        if (this.shooters.isEmpty()) {return; }
        int index = (int) (Math.random() * this.shooters.size());
        EnemyShip shooter = this.shooters.get(index);

        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            bullets.add(BulletPool.getBullet(shooter.getPositionX()
                    + shooter.width / 2, shooter.getPositionY(), BULLET_SPEED));
        }
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
                    column.get(shipIndexInColumn).destroy();
                    this.logger.info("Destroyed ship in (" + i + "," + shipIndexInColumn + ")");
                    this.shipCount--;

                    // Updates the list of ships that can shoot the player.
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
                return;
            }
        }
    }
    /**
     * Gets the ship on a given column that will be in charge of shooting.
     *
     * @param column
     * Column to search.
     * @return New shooter ship.
     */
    public final EnemyShip getNextShooter(final List<EnemyShip> column) {
        Iterator<EnemyShip> iterator = column.iterator();
        EnemyShip nextShooter = null;
        while (iterator.hasNext()) {
            EnemyShip checkShip = iterator.next();
            if (checkShip != null && !checkShip.isDestroyed())
                nextShooter = checkShip;
        }

        return nextShooter;
    }

    /**
     * Returns an iterator over the ships in the formation.
     *
     * @return Iterator over the enemy ships.
     */
    @Override
    public final Iterator<EnemyShip> iterator() {
        Set<EnemyShip> enemyShipsList = new HashSet<EnemyShip>();

        for (List<EnemyShip> column : this.enemyShips)
            for (EnemyShip enemyShip : column)
                enemyShipsList.add(enemyShip);

        return enemyShipsList.iterator();
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
                    enemyShip.destroy();
                    destroyed++;
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
        this.isSlowedDown = true;
        this.slowDownCount = 0;
        this.logger.info("Enemy formation slowed down!");
    }

    /**
     * Gets the current movement speed based on slowdown status.
     *
     * @return Current X_SPEED value
     */
    private int getCurrentXSpeed() {
        if (isSlowedDown) {
            return SLOWED_X_SPEED;
        }
        return ORIGINAL_X_SPEED;
    }

    /**
     * Updates slowdown counter and checks if effect should end.
     * Call this in the update() method when formation moves.
     */
    private void updateSlowdown() {
        if (isSlowedDown) {
            slowDownCount++;
            if (slowDownCount >= SLOWDOWN_DURATION) {
                isSlowedDown = false;
                slowDownCount = 0;
                this.logger.info("Slowdown effect ended.");
            }
        }
    }

    public final void clear() {
        for (List<EnemyShip> column : this.enemyShips) {
            column.clear();
        }
        this.enemyShips.clear();
        this.shipCount = 0;
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
     * Applies a color to all ships based on the level number.
     * @param level The current level.
     */
    public void applyEnemyColorByLevel(final Level level) {
        if (level == null) return;
        final int lv = level.getLevel();
        applyEnemyColor(getColorForLevel(lv));
    }

    /**
     * Gets the appropriate color for a given level number.
     * @param levelNumber The level number.
     * @return The color for that level.
     */
    private Color getColorForLevel(final int levelNumber) {
        switch (levelNumber) {
            case 1: return new Color(0x3DDC84); // green
            case 2: return new Color(0x00BCD4); // cyan
            case 3: return new Color(0xFF4081); // pink
            case 4: return new Color(0xFFC107); // amber
            case 5: return new Color(0x9C27B0); // purple
            case 6: return new Color(0xFF5722); // deep orange
            case 7: return new Color(0x8BC34A); // light green
            case 8: return new Color(0x03A9F4); // light blue
            case 9: return new Color(0xE91E63); // magenta
            case 10: return new Color(0x607D8B); // blue gray
            default: return Color.WHITE;
        }
    }
}