package engine.Renderer;

import engine.BackBuffer;
import engine.FontPack;
import engine.ItemHUDManager;
import entity.Ship;

import java.awt.*;

/**
 * Handles all on-screen HUD rendering such as scores, coins, and timers.
 * Acts as a sub-view in the MVC structure.
 */
public final class HUDRenderer {

    private final BackBuffer backBuffer;
    private final FontPack fontPack;
    private final EntityRenderer entityRenderer;

    public HUDRenderer(BackBuffer backBuffer, FontPack fontPack, EntityRenderer entityRenderer) {
        this.backBuffer = backBuffer;
        this.fontPack = fontPack;
        this.entityRenderer = entityRenderer;
    }

    /** Draw score. */
    public void drawScore(final int screenWidth, final int score, final int y) {
        Graphics g = backBuffer.getGraphics();
        Font font = fontPack.getRegular();
        g.setFont(font);
        g.setColor(Color.WHITE);
        String scoreString = String.format("P1:%04d", score);
        g.drawString(scoreString, screenWidth - 120, y);
    }

    /** Draw elapsed time on screen. */
    public void drawTime(final int screenHeight, final long milliseconds) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.GRAY);

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        // 글자 높이 가져오기
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();

        // 화면 가장 아래에서 글자 높이만큼 위로 올리기
        int y = screenHeight - fontHeight;

        String timeString = String.format("Time: %02d:%02d", minutes, seconds);
        g.drawString(timeString, 10, y);
    }

    /** Draw current coin count on screen (bottom-center). */
    public void drawCoin(final int screenWidth, final int screenHeight, final int coin) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);

        String coinString = String.format("%03d$", coin);
        int textWidth = fontPack.getRegularMetrics().stringWidth(coinString);
        int x = screenWidth / 2 - textWidth / 2;
        int y = screenHeight - 50;

        g.drawString(coinString, x, y);
    }

    /** Draw number of remaining lives for Player 1. */
    public void drawLivesP1(final int screenWidth, final int screenHeight,final int lives) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);
        g.drawString("P1:", 10, 25);
        Ship dummyShip = new Ship(0, 0, Color.GREEN);
        for (int i = 0; i < lives; i++) {
            entityRenderer.drawEntity(dummyShip, 40 + 35 * i, 10);
        }
    }

    /** Draw number of remaining lives for Player 2. */
    public void drawLivesP2(final int screenWidth, final int screenHeight, final int lives) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);
        g.drawString("P2:", 10, 55);
        Ship dummyShip = new Ship(0, 0, Color.PINK);
        for (int i = 0; i < lives; i++) {
            entityRenderer.drawEntity(dummyShip, 40 + 35 * i, 40);
        }
    }

    /** Draw all item icons on HUD. */
    public void drawItemsHUD(final int screenWidth, final int screeHeight) {
        Graphics g = backBuffer.getGraphics();
        ItemHUDManager hud = ItemHUDManager.getInstance();
        hud.setHUDPositions(screeHeight);
        hud.initialize(screenWidth);
        hud.drawItems(g);
    }

    /** Draw current level name (bottom-left). */
    public void drawLevel(final int seperateLine, final String levelName) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);
        // 글자 높이 가져오기
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();

        // 화면 가장 아래에서 글자 높이만큼 위로 올리기
        int y = seperateLine + fontHeight;
        g.drawString(levelName, 20, y);
    }

    /** Draw achievement popup at the top center of the screen. */
    public void drawAchievementPopup(final int screenWidth, final String text) {
        Graphics g = backBuffer.getGraphics();
        int popupWidth = 250, popupHeight = 50;
        int x = screenWidth / 2 - popupWidth / 2;
        int y = 80;

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, popupWidth, popupHeight, 15, 15);

        g.setColor(Color.YELLOW);
        g.drawRoundRect(x, y, popupWidth, popupHeight, 15, 15);

        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);
        int textWidth = fontPack.getRegularMetrics().stringWidth(text);
        g.drawString(text, (screenWidth - textWidth) / 2, y + popupHeight / 2 + 5);
    }

    /** Draw health popup (green if heal, red if damage). */
    public void drawHealthPopup(final int screenWidth, final String text) {
        Graphics g = backBuffer.getGraphics();
        int popupWidth = 250, popupHeight = 40;
        int x = screenWidth / 2 - popupWidth / 2;
        int y = 100;

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, popupWidth, popupHeight, 15, 15);

        g.setColor(text.startsWith("+") ? new Color(50, 255, 50) : new Color(255, 50, 50));
        g.setFont(fontPack.getFontBig());
        int textWidth = fontPack.getBigMetrics().stringWidth(text);
        g.drawString(text, (screenWidth - textWidth) / 2, y + popupHeight / 2 + 5);
    }
}
