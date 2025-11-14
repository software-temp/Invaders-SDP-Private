package engine;

public class Coin {
    /** Player's name. */
    private String name;
    /** Score points. */
    private int coin;

    public Coin(final String name, final int coin) {
        this.name = name;
        this.coin = coin;
    }

    /**
     * Getter for the player's name
     * @return Name of the player.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Getter for the player's score.
     * @return High score.
     */
    public final int getCoin() {
        return this.coin;
    }
}
