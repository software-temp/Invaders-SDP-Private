package entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the object recycling logic of the BulletPool.
 */
class BulletPoolTest {

    // Use reflection to access the private static 'pool' field in BulletPool.
    private static Set<Bullet> getInternalPool() throws Exception {
        Field poolField = BulletPool.class.getDeclaredField("pool");
        poolField.setAccessible(true);
        return (Set<Bullet>) poolField.get(null); // 'null' signifies a static field.
    }

    @BeforeEach
    void setUp() throws Exception {
        // Forcibly clear the BulletPool before each test to ensure no side-effects.
        getInternalPool().clear();
    }

    @Test
    void testGetBullet_New() {
        // 1. Given an empty pool,
        // 2. When getBullet() is called, it should return a new Bullet object.
        Bullet b1 = BulletPool.getBullet(10, 10, 1);
        assertNotNull(b1);
        assertEquals(10 - b1.getWidth() / 2, b1.getPositionX()); // getBullet adjusts the X position.
        assertEquals(10, b1.getPositionY());
    }

    @Test
    void testRecycleAndReuse() {
        // 1. Given a manually created Bullet,
        Bullet b1 = new Bullet(100, 100, 100, Color.WHITE);
        Set<Bullet> bulletsToRecycle = new HashSet<>();
        bulletsToRecycle.add(b1);

        // 2. When it is recycled into the pool,
        BulletPool.recycle(bulletsToRecycle);

        // 3. And a new Bullet is requested from the pool,
        Bullet b2 = BulletPool.getBullet(50, 50, 5);

        // 4. b2 should be the *exact same object* as b1 (assertSame).
        assertSame(b1, b2, "A recycled object should be returned.");

        // 5. And b2's state should be reset to the new values requested by getBullet().
        assertEquals(50 - b2.getWidth() / 2, b2.getPositionX(), "X position should be reset.");
        assertEquals(50, b2.getPositionY(), "Y position should be reset.");
        assertEquals(5.0, b2.getSpeed(), "Speed should be reset.");
    }

    @Test
    void testPoolEmptiesAfterGet() throws Exception {
        // 1. Given an object recycled into the pool.
        Set<Bullet> bulletsToRecycle = new HashSet<>();
        bulletsToRecycle.add(new Bullet(1, 1, 1, Color.WHITE));
        BulletPool.recycle(bulletsToRecycle);
        assertEquals(1, getInternalPool().size(), "Pool should contain one object.");

        // 2. When that object is retrieved.
        BulletPool.getBullet(2, 2, 2);

        // 3. The pool should be empty again.
        assertEquals(0, getInternalPool().size(), "Pool should be empty after object is retrieved.");
    }
}