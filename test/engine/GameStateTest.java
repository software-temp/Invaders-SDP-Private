package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the coin-related logic of the GameState class.
 */
class GameStateTest {

    private GameState gameState;

    @BeforeEach
    void setUp() {
        // Starts each test with a default GameState having 100 coins.
        gameState = new GameState(1, 0, 3, 3, 0, 0, 100);
    }

    @Test
    void testAddCoins() {
        // 1. When adding coins,
        gameState.addCoins(50);
        // 2. The total should be 150.
        assertEquals(150, gameState.getCoin(), "Adding coins should be reflected accurately.");
    }

    @Test
    void testAddNegativeCoins() {
        // 1. When attempting to "add" negative coins,
        gameState.addCoins(-10);
        // 2. The coin count should not change (remains 100).
        assertEquals(100, gameState.getCoin(), "Adding negative coins should not be allowed.");
    }

    @Test
    void testDeductCoins_Success() {
        // 1. When deducting 50 coins,
        boolean result = gameState.deductCoins(50);
        // 2. It should return true (success) and the coin count should be 50.
        assertTrue(result, "Coin deduction should be successful.");
        assertEquals(50, gameState.getCoin(), "Coin deduction should be reflected accurately.");
    }

    @Test
    void testDeductCoins_InsufficientFunds() {
        // 1. When attempting to deduct 150 (more than available),
        boolean result = gameState.deductCoins(150);
        // 2. It should return false (failure) and the coin count should remain 100.
        assertFalse(result, "Cannot deduct more coins than available.");
        assertEquals(100, gameState.getCoin(), "Coin count should not change on failed deduction.");
    }

    @Test
    void testDeductCoins_Negative() {
        // 1. When attempting to "deduct" negative coins,
        boolean result = gameState.deductCoins(-10);
        // 2. It should return false (failure) and the coin count should remain 100.
        assertFalse(result, "Cannot deduct negative coins.");
        assertEquals(100, gameState.getCoin(), "Deducting negative coins should be ignored.");
    }
}