package entity;

import engine.Core;

public class GameConstant {
    /** Height of the interface separation line. */
    public static int STAT_SEPARATION_LINE_HEIGHT;
    /** Height of the items separation line (above items). */
    public static int ITEMS_SEPARATION_LINE_HEIGHT;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static void initialize(int screenWidth, int screenHeight){
        STAT_SEPARATION_LINE_HEIGHT = (int) (screenHeight * 0.08);
        ITEMS_SEPARATION_LINE_HEIGHT = (int) (Core.FRAME_HEIGHT * 0.9);
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;
    }
}
