package main.entity;

/**
 * Defines a contract for enemy formation movement strategies.
 */
interface IMovementStrategy {

    /**
     * Updates the movement logic
     */
    void updateMovement();

    /**
     * Activates the slowdown status effect on this movement strategy.
     */
    void activateSlowdown();
}