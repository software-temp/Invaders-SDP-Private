package entity;


import engine.Core;

public final class GameConstant {

    /** Height of the interface separation line. */
    public static int STAT_SEPARATION_LINE_HEIGHT;
    /** Height of the items separation line (above items). */
    public static int ITEMS_SEPARATION_LINE_HEIGHT;
	/** Width of the Screen */
	public static int SCREEN_WIDTH;
	/** Width of the Screen */
	public static int SCREEN_HEIGHT;
	/** Max fps of current screen. */
	public static final int FPS = 60;
	/** Set if this run is for debugging */
	public static final boolean isTest = false;

    private GameConstant() {}
    private static boolean initialized = false;

    public static void initialize(int screenWidth, int screenHeight) {
        if (initialized) {
            throw new IllegalStateException("GameConstant has already been initialized.");
        }
        STAT_SEPARATION_LINE_HEIGHT = (int) (screenHeight * 0.08);
        ITEMS_SEPARATION_LINE_HEIGHT = (int) (screenHeight * 0.9);
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;

        initialized = true;
    }
}
