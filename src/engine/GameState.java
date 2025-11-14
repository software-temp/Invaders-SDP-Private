package engine;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameState {

	/** Current game level. */
	private int level;
	/** Current score. */
	private int score;
	/** Lives currently remaining. */
	private int livesRemaining;
	private int livesRemainingP2;
	/** Bullets shot until now. */
	private int bulletsShot;
	/** Ships destroyed until now. */
	private int shipsDestroyed;
    /** Current coin. */
    private int coin;


	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Current game level.
	 * @param score
	 *            Current score.
     * @param coin
     *            Current coin.
	 * @param livesRemaining
	 *            Lives currently remaining.
	 * @param livesRemainingP2
	 *            Lives currently remainingP2.
	 * @param bulletsShot
	 *            Bullets shot until now.
	 * @param shipsDestroyed
	 *            Ships destroyed until now.
	 */
	public GameState(final int level, final int score,
			final int livesRemaining,final int livesRemainingP2, final int bulletsShot,
			final int shipsDestroyed, final int coin) {
		this.level = level;
		this.score = score;
		this.livesRemaining = livesRemaining;
		this.livesRemainingP2 = livesRemainingP2;
		this.bulletsShot = bulletsShot;
        this.shipsDestroyed = shipsDestroyed;
        this.coin = coin;
		    }
	/**
	 * @return the level
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public final int getScore() {
		return score;
	}

	/**
	 * @return the livesRemaining
	 */
	public final int getLivesRemaining() {
		return livesRemaining;
	}

	public final int getLivesRemainingP2() {
		return livesRemainingP2;
	}

	/**
	 * @return the bulletsShot
	 */
	public final int getBulletsShot() {
		return bulletsShot;
	}

	/**
	 * @return the shipsDestroyed
	 */
	public final int getShipsDestroyed() {
		return shipsDestroyed;
	}

    public final int getCoin() { return coin; }

	public final boolean deductCoins(final int amount) {
		if (amount < 0) {
			return false;
		}
		if (this.coin >= amount) {
			this.coin -= amount;
			return true;
		}
		return false;
	}

	public final void addCoins(final int amount) {
		if (amount > 0) {
			this.coin += amount;
		}
	}

	public final void setCoins(final int amount) {
		if (amount >= 0) {
			this.coin = amount;
		}
	}
}
