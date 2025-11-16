package entity; // ◀ Must match the original package

// Import required classes for JUnit and AWT Color
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Ship class.
 * (Testing object creation and movement)
 */
class ShipTest {

    // This is the BASE_SPEED constant defined in Ship.java
    private static final int BASE_SPEED = 2;
    // This is the default shooting interval, used for mocking
    private static final int SHOOTING_INTERVAL = 750;

    /**
     * This method runs before *every* test.
     * It calls resetAllItems() to set all static item levels in ShopItem to 0.
     * This guarantees that ShopItem.getSHIPSpeedCOUNT() returns 0,
     * making the movement tests stable and predictable.
     */
    @BeforeEach
    void setUp() {
        ShopItem.resetAllItems();
    }

    /**
     * [Creation Test]
     * Checks if the initial position and color are set correctly when a Ship object is created.
     */
    @Test
    void testShipCreation() {
        // --- 1. Arrange ---
        int startX = 100;
        int startY = 150;
        Color testColor = Color.CYAN; // A test color

        // --- 2. Act ---
        // Create the Ship object.
        Ship ship = new Ship(startX, startY, testColor);

        // --- 3. Assert ---
        // Verify that the values in the object match the values passed to the constructor.
        assertEquals(startX, ship.getPositionX(), "The X coordinate was not set correctly.");
        assertEquals(startY, ship.getPositionY(), "The Y coordinate was not set correctly.");
        assertEquals(testColor, ship.getColor(), "The Color was not set correctly.");
    }
    /**
     * [Movement Test - Right]
     * Because setUp() runs first, ShopItem.getSHIPSpeedCOUNT() is 0.
     * The logic 'SPEED * (1 + 0/10)' simplifies to just 'SPEED'.
     * This test is now stable.
     */
    @Test
    void testShipMovement_MoveRight() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        int initialX = ship.getPositionX();
        int speed = ship.getSpeed(); // This gets the base SPEED (2)

        // --- 2. Act ---
        ship.moveRight();

        // --- 3. Assert ---
        assertEquals(initialX + speed, ship.getPositionX(), "Rightward movement is incorrect.");
    }

    /**
     * [Movement Test - Left]
     * This test is also stable now, thanks to setUp().
     */
    @Test
    void testShipMovement_MoveLeft() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        int initialX = ship.getPositionX();
        int speed = ship.getSpeed();

        // --- 2. Act ---
        ship.moveLeft();

        // --- 3. Assert ---
        assertEquals(initialX - speed, ship.getPositionX(), "Leftward movement is incorrect.");
    }

    /**
     * [Movement Test - Up]
     * This test is also stable now, thanks to setUp().
     */
    @Test
    void testShipMovement_MoveUp() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        int initialY = ship.getPositionY();
        int speed = ship.getSpeed();

        // --- 2. Act ---
        ship.moveUp();

        // --- 3. Assert ---
        assertEquals(initialY - speed, ship.getPositionY(), "Upward movement is incorrect.");
    }

    /**
     * [Movement Test - Down]
     * This test is also stable now, thanks to setUp().
     */
    @Test
    void testShipMovement_MoveDown() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        int initialY = ship.getPositionY();
        int speed = ship.getSpeed();

        // --- 2. Act ---
        ship.moveDown();

        // --- 3. Assert ---
        assertEquals(initialY + speed, ship.getPositionY(), "Downward movement is incorrect.");
    }


    /*
     * ======================================
     * Shooting Tests
     * (These are also stable now because setUp() resets the rapidFireLevel,
     * so getShootingInterval() always returns the default 750ms)
     * ======================================
     */

    @Test
    void testShoot_Success() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        Set<Bullet> bullets = new HashSet<>();

        // --- 2. Act ---
        boolean shot = ship.shoot(bullets);

        // --- 3. Assert ---
        assertTrue(shot, "Should be successful in shooting.");
        assertEquals(1, bullets.size(), "A bullet should be added to the set.");
    }

    @Test
    void testShoot_Cooldown() {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        Set<Bullet> bullets = new HashSet<>();
        ship.shoot(bullets); // First shot (success)
        assertEquals(1, bullets.size());

        // --- 2. Act ---
        boolean shot2 = ship.shoot(bullets); // Fire immediately

        // --- 3. Assert ---
        assertFalse(shot2, "Should not be able to shoot during cooldown.");
        assertEquals(1, bullets.size(), "No additional bullet should be fired.");
    }

    @Test
    void testShoot_AfterCooldown() throws InterruptedException {
        // --- 1. Arrange ---
        Ship ship = new Ship(100, 100, Color.GREEN);
        Set<Bullet> bullets = new HashSet<>();
        ship.shoot(bullets); // First shot (success)

        // --- 2. Act ---
        Thread.sleep(SHOOTING_INTERVAL + 50); // Wait for cooldown to expire
        boolean shot2 = ship.shoot(bullets); // Fire second shot

        // --- 3. Assert ---
        assertTrue(shot2, "Should be able to shoot again after cooldown.");
        assertEquals(2, bullets.size(), "The second bullet should be added to the set.");
    }
}