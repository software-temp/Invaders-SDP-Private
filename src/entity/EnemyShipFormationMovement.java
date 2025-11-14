package main.entity;

import java.util.logging.Logger;
import main.engine.Core;

/**
 * Implements a diagonal movement pattern for the formation,
 * reversing direction upon hitting screen boundaries.
 * This class also manages the slowdown status effect.
 */
public class EnemyShipFormationMovement implements IMovementStrategy {

    /**
     * Defines the possible movement directions.
     */
    private enum Direction {
        DOWN_RIGHT, DOWN_LEFT, UP_RIGHT, UP_LEFT
    }

    /** Current movement direction. */
    private Direction currentDirection;
    /** Application logger. */
    private Logger logger;

    /** Initial vertical position to check for top boundary. */
    private static final int INIT_POS_Y = 100;
    /** Base vertical speed. */
    private static final int Y_SPEED = 4;
    /** Margin from the screen edges. */
    private static final int SIDE_MARGIN = 20;

    /** Default horizontal speed. */
    private static final int ORIGINAL_X_SPEED = 8;
    /** Horizontal speed when slowed. */
    private static final int SLOWED_X_SPEED = 4;
    /** Duration of the slowdown effect in movement cycles. */
    private static final int SLOWDOWN_DURATION = 18;


    /** Flag indicating if the slowdown effect is active. */
    private boolean isSlowedDown;
    /** Counter for the remaining duration of the slowdown effect. */
    private int slowDownCount;
    private EnemyShipFormationModel model;
    /** The maximum Y-coordinate (bottom boundary) the formation is allowed to move to. */
    private int formationBottomLimit;
    /** The total width of the playable area, used for calculating the right boundary. */
    private int formationScreenWidth;
    /**
     * Initializes the movement logic.
     * @param model The parent formation model this strategy will control.
     */
    public EnemyShipFormationMovement(EnemyShipFormationModel model, int bottomLimit, int screenWidth) {
        this.model = model;
        this.logger = Core.getLogger();
        this.currentDirection = Direction.DOWN_RIGHT;
        this.isSlowedDown = false;
        this.slowDownCount = 0;
        this.formationBottomLimit = bottomLimit;
        this.formationScreenWidth = screenWidth;
    }

    /**
     * Updates the formation's position based on the diagonal movement logic.
     *
     */
    @Override
    public void updateMovement() {

        updateSlowdownInternal();

        int positionX = model.getPositionX();
        int positionY = model.getPositionY();
        int width = model.getWidth();
        int height = model.getHeight();

        boolean isAtBottom = positionY + height > formationBottomLimit;
        boolean isAtRightSide = positionX + width >= formationScreenWidth - SIDE_MARGIN;
        boolean isAtLeftSide = positionX <= SIDE_MARGIN;
        boolean isAtTop = positionY <= INIT_POS_Y;

        if (currentDirection == Direction.DOWN_RIGHT) {
            if (isAtBottom && isAtRightSide) currentDirection = Direction.UP_LEFT;
            else if (isAtBottom) currentDirection = Direction.UP_RIGHT;
            else if (isAtRightSide) currentDirection = Direction.DOWN_LEFT;
        } else if (currentDirection == Direction.DOWN_LEFT) {
            if (isAtBottom && isAtLeftSide) currentDirection = Direction.UP_RIGHT;
            else if (isAtBottom) currentDirection = Direction.UP_LEFT;
            else if (isAtLeftSide) currentDirection = Direction.DOWN_RIGHT;
        } else if (currentDirection == Direction.UP_RIGHT) {
            if (isAtTop && isAtRightSide) currentDirection = Direction.DOWN_LEFT;
            else if (isAtTop) currentDirection = Direction.DOWN_RIGHT;
            else if (isAtRightSide) currentDirection = Direction.UP_LEFT;
        } else if (currentDirection == Direction.UP_LEFT) {
            if (isAtTop && isAtLeftSide) currentDirection = Direction.DOWN_RIGHT;
            else if (isAtTop) currentDirection = Direction.DOWN_LEFT;
            else if (isAtLeftSide) currentDirection = Direction.UP_RIGHT;
        }

        int movementX = 0;
        int movementY = 0;
        int currentXSpeed = getCurrentXSpeedInternal();

        if (currentDirection == Direction.DOWN_RIGHT) {
            movementX = currentXSpeed;
            movementY = Y_SPEED;
        } else if (currentDirection == Direction.DOWN_LEFT) {
            movementX = -currentXSpeed;
            movementY = Y_SPEED;
        } else if (currentDirection == Direction.UP_RIGHT) {
            movementX = currentXSpeed;
            movementY = -Y_SPEED;
        } else if (currentDirection == Direction.UP_LEFT) {
            movementX = -currentXSpeed;
            movementY = -Y_SPEED;
        }

        model.setPosition(positionX + movementX, positionY + movementY);

        model.moveAllShips(movementX, movementY);
    }

    /**
     * Gets the current horizontal speed, accounting for slowdown.
     * @return The horizontal speed (pixels per move).
     */
    private int getCurrentXSpeedInternal() {
        if (isSlowedDown) {
            return SLOWED_X_SPEED;
        }
        return ORIGINAL_X_SPEED;
    }

    /**
     * Updates the internal slowdown counter and deactivates the effect if it expires.
     */
    private void updateSlowdownInternal() {
        if (isSlowedDown) {
            slowDownCount++;
            if (slowDownCount >= SLOWDOWN_DURATION) {
                isSlowedDown = false;
                slowDownCount = 0;
                this.logger.info("Slowdown effect ended.");
            }
        }
    }

    /**
     * Activates the slowdown effect, resetting its duration.
     */
    @Override
    public void activateSlowdown() {
        this.isSlowedDown = true;
        this.slowDownCount = 0;
        this.logger.info("Enemy formation slowed down!");
    }
}