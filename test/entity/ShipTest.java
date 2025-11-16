package entity; // ◀ Must match the original package

// Import required classes for JUnit and AWT Color
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
     * [Movement Test - Base Speed]
     * Verifies movement when ShopItem speed upgrade is 0.
     * This test mocks the static ShopItem.getSHIPSpeedCOUNT() method.
     */
    @Test
    void testShipMovement_BaseSpeed() {
        // Use try-with-resources to create a static mock for ShopItem
        try (MockedStatic<ShopItem> shopItemMock = Mockito.mockStatic(ShopItem.class)) {
            // --- 1. Arrange ---
            // Force ShopItem.getSHIPSpeedCOUNT() to return 0 for this test
            shopItemMock.when(ShopItem::getSHIPSpeedCOUNT).thenReturn(0);
            // We also need to mock the interval for the Ship's constructor
            shopItemMock.when(ShopItem::getShootingInterval).thenReturn(SHOOTING_INTERVAL);

            Ship ship = new Ship(100, 100, Color.GREEN);
            int initialX = ship.getPositionX();
            int initialY = ship.getPositionY();

            // --- 2. Act ---
            // The formula is SPEED * (1 + shipspeed/10).
            // Here: 2 * (1 + 0/10) = 2
            ship.moveRight();
            ship.moveDown();

            // --- 3. Assert ---
            assertEquals(initialX + BASE_SPEED, ship.getPositionX(), "Right movement (base speed) is incorrect.");
            assertEquals(initialY + BASE_SPEED, ship.getPositionY(), "Down movement (base speed) is incorrect.");

            // --- 2. Act (Left/Up) ---
            ship.moveLeft(); // Expected movement: -2
            ship.moveUp();   // Expected movement: -2

            // --- 3. Assert (Left/Up) ---
            // The ship should be back at its starting position
            assertEquals(initialX, ship.getPositionX(), "Left movement (base speed) is incorrect.");
            assertEquals(initialY, ship.getPositionY(), "Up movement (base speed) is incorrect.");
        }
    }

    /**
     * [Movement Test - Upgraded Speed]
     * Verifies movement when ShopItem speed upgrade is non-zero (e.g., 20).
     * This directly tests the logic reviewers were concerned about.
     */
    @Test
    void testShipMovement_UpgradedSpeed() {
        try (MockedStatic<ShopItem> shopItemMock = Mockito.mockStatic(ShopItem.class)) {
            // --- 1. Arrange ---
            // Force ShopItem.getSHIPSpeedCOUNT() to return 20
            shopItemMock.when(ShopItem::getSHIPSpeedCOUNT).thenReturn(20);
            // Mock for constructor
            shopItemMock.when(ShopItem::getShootingInterval).thenReturn(SHOOTING_INTERVAL);

            Ship ship = new Ship(100, 100, Color.GREEN);
            int initialX = ship.getPositionX();

            // Calculate expected speed based on Ship.java logic: SPEED * (1 + shipspeed / 10)
            int expectedSpeed = BASE_SPEED * (1 + 20 / 10); // 2 * (1 + 2) = 6

            // --- 2. Act ---
            ship.moveRight();

            // --- 3. Assert ---
            // The ship should move by the calculated upgraded speed (6), not the base speed (2).
            assertEquals(initialX + expectedSpeed, ship.getPositionX(), "Right movement (upgraded speed) is incorrect.");
        }
    }

    /*
     * ======================================
     * Shooting Tests (also require Mockito)
     * ======================================
     */

    @Test
    void testShoot_Success() {
        try (MockedStatic<ShopItem> shopItemMock = Mockito.mockStatic(ShopItem.class)) {
            // --- 1. Arrange ---
            // Mock dependencies for constructor (shootingInterval) and shoot() (bulletCount)
            shopItemMock.when(ShopItem::getShootingInterval).thenReturn(SHOOTING_INTERVAL);
            shopItemMock.when(ShopItem::getMultiShotBulletCount).thenReturn(1);

            Ship ship = new Ship(100, 100, Color.GREEN);
            Set<Bullet> bullets = new HashSet<>();

            // --- 2. Act ---
            boolean shot = ship.shoot(bullets);

            // --- 3. Assert ---
            assertTrue(shot, "Should be successful in shooting.");
            assertEquals(1, bullets.size(), "A bullet should be added to the set.");
        }
    }

    @Test
    void testShoot_Cooldown() {
        try (MockedStatic<ShopItem> shopItemMock = Mockito.mockStatic(ShopItem.class)) {
            // --- 1. Arrange ---
            shopItemMock.when(ShopItem::getShootingInterval).thenReturn(SHOOTING_INTERVAL);
            shopItemMock.when(ShopItem::getMultiShotBulletCount).thenReturn(1);

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
    }

    @Test
    void testShoot_AfterCooldown() throws InterruptedException {
        try (MockedStatic<ShopItem> shopItemMock = Mockito.mockStatic(ShopItem.class)) {
            // --- 1. Arrange ---
            shopItemMock.when(ShopItem::getShootingInterval).thenReturn(SHOOTING_INTERVAL);
            shopItemMock.when(ShopItem::getMultiShotBulletCount).thenReturn(1);

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
}