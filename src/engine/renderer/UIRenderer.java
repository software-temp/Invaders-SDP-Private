package engine.renderer;

import engine.Achievement;
import engine.BackBuffer;
import engine.FontPack;
import engine.Score;

import java.util.List;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

public final class UIRenderer {
    private final BackBuffer backBuffer;
    private final FontPack fontPack;

    public UIRenderer(BackBuffer backBuffer, FontPack fontPack) {
        this.backBuffer = backBuffer;
        this.fontPack = fontPack;
    }

    public void drawHorizontalLine(final int screenWidth, final int y) {
        Graphics g = backBuffer.getGraphics();
        g.setColor(Color.GREEN);
        g.drawLine(0, y, screenWidth, y);
        g.drawLine(0, y + 1, screenWidth, y + 1);
    }

    public void drawTitle(final int screenWidth, final int screenHeight) {
        Graphics g = backBuffer.getGraphics();

        g.setFont(fontPack.getFontBig());
        g.setColor(Color.GREEN);
        String title = "Space Invaders extension";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(title);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight / 3;
        g.drawString(title, x, y);

        g.setFont(fontPack.getRegular());
        g.setColor(Color.GREEN);
        String str = "with temp";
        fm = g.getFontMetrics();
        textWidth = fm.stringWidth(str);
        x = (screenWidth - textWidth) / 2 + 200;
        y = screenHeight / 3 + 30;
        g.drawString(str, x, y);

        g.setFont(fontPack.getRegular());
        g.setColor(Color.YELLOW);
        String explain1 = "Player 1: move: W.A.S.D / shoot: space";

        fm = g.getFontMetrics();
        textWidth = fm.stringWidth(explain1);
        x = (screenWidth - textWidth) / 2;
        y = screenHeight / 2;
        g.drawString(explain1, x, y);

        String explain2 = "Player 2: move: arrow keys / shoot: enter";
        fm = g.getFontMetrics();
        textWidth = fm.stringWidth(explain2);
        x = (screenWidth - textWidth) / 2;
        y = screenHeight / 2 + 30;
        g.drawString(explain2, x, y);
    }

    /** Draws main menu options with pulsing selection effect. */
    public void drawMenu(final int screenWidth, final int screenHeight, final int option) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());

        String[] options = { "Play", "High Scores", "Shop", "Achievements","Exit"};

        // Pulse effect for selection
        float pulse = (float) ((Math.sin(System.currentTimeMillis() / 200.0) + 1.0) / 2.0);
        Color pulseColor = new Color(0, 0.5f + pulse * 0.5f, 0);

        int baseY = screenHeight * 2 / 3;
        int spacing = fontPack.getRegularMetrics().getHeight();

        int selectedIndex = switch (option){
            case 2 -> 0; // Play
            case 3 -> 1; // High Scores
            case 4 -> 2; // Shop
            case 6 -> 3; // Achievements
            case 0 -> 4; // Exit

            default -> -1; // none (ex. sound button focus)
        };

        for (int i = 0; i < options.length; i++) {
            if (i == selectedIndex)
                g.setColor(pulseColor);
            else
                g.setColor(Color.WHITE);

            int textWidth = fontPack.getRegularMetrics().stringWidth(options[i]);
            int x = (screenWidth - textWidth) / 2;
            int y = baseY + spacing * i;

            g.drawString(options[i], x, y);
        }
    }

    /**
     * Draws the boss's area-wide attack warning. (View logic)
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     * @param safeZoneColumn Safe zone index (0-9)
     */
    public void drawApocalypseWarning(final int screenWidth, final int screenHeight, final int safeZoneColumn) {
        Graphics g = backBuffer.getGraphics();

        int columnWidth = screenWidth / 10;

        // 100/255 (approx. 40%) transparency color
        Color attackColor = new Color(255, 0, 0, 100);
        Color safeColor = new Color(255, 255, 255, 100);

        for (int i = 0; i < 10; i++) {
            if (i == safeZoneColumn) {
                g.setColor(safeColor); // Safe zone (white)
            } else {
                g.setColor(attackColor); // Attack zone (red)
            }

            // Draw rectangles for the full screen height.
            g.fillRect(i * columnWidth, 0, columnWidth, screenHeight);
        }
    }

    /** Draws game results on the end screen.*/
    public void drawResults(final int screenWidth, final int screenHeight, final int score, final int livesRemaining,
                            final int shipsDestroyed, final float accuracy, final boolean isNewRecord) {

        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);

        String scoreString = String.format("Score: %04d", score);
        String livesRemainingString = "Lives remaining: " + livesRemaining;
        String shipsDestroyedString = "Enemies destroyed: " + shipsDestroyed;
        String accuracyString = String.format("Accuracy: %.2f%%", accuracy * 100);

        int baseY = isNewRecord ? screenHeight / 4 : screenHeight / 2;
        int spacing = fontPack.getRegularMetrics().getHeight() * 2;

        g.drawString(scoreString, centerX(screenWidth, g, scoreString), baseY);
        g.drawString(livesRemainingString, centerX(screenWidth, g, livesRemainingString), baseY + spacing);
        g.drawString(shipsDestroyedString, centerX(screenWidth, g, shipsDestroyedString), baseY + spacing * 2);
        g.drawString(accuracyString, centerX(screenWidth, g, accuracyString), baseY + spacing * 3);
    }

    /**
     * Draws interactive name input after new record.
     */
    public void drawNameInput(final int screenWidth, final int screenHeight, final char[] name, final int nameCharSelected) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());

        String newRecordString = "NEW RECORD!";
        String promptString = "Enter your initials:";

        g.setColor(Color.GREEN);
        g.drawString(newRecordString, centerX(screenWidth, g, newRecordString),
                screenHeight / 4 + fontPack.getRegularMetrics().getHeight() * 10);

        g.setColor(Color.WHITE);
        g.drawString(promptString, centerX(screenWidth, g, promptString),
                screenHeight / 4 + fontPack.getRegularMetrics().getHeight() * 12);

        // --- Draw name characters ---
        int baseY = screenHeight / 4 + fontPack.getRegularMetrics().getHeight() * 14;
        int x = screenWidth / 2 - 50;

        for (int i = 0; i < name.length; i++) {
            if (i == nameCharSelected) g.setColor(Color.GREEN);
            else g.setColor(Color.WHITE);

            String ch = Character.toString(name[i]);
            g.drawString(ch, x + i * 35, baseY);
        }
    }

    public void drawGameOver(final int screenWidth, final int screenHeight, final boolean acceptsInput, final boolean isNewRecord) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontBig());
        String gameOverText = "GAME OVER";
        String continueText = "Press SPACE to play again, ESC to exit";

        int baseY = isNewRecord ? screenHeight / 4 : screenHeight / 2;

        /** title */
        g.setColor(Color.GREEN);
        g.drawString(gameOverText,
                centerX(screenWidth, g, gameOverText),
                baseY - fontPack.getBigMetrics().getHeight() * 2);

        /** instructions */
        g.setFont(fontPack.getRegular());
        g.setColor(acceptsInput ? Color.GREEN : Color.GRAY);
        g.drawString(continueText,
                centerX(screenWidth, g, continueText),
                screenHeight / 2 + fontPack.getRegularMetrics().getHeight() * 10);
    }

    /** high score title */
    public void drawHighScoreMenu(final int screenWidth, final int screenHeight) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontBig());
        String title = "High Scores";
        String instructions = "Press SPACE to return";

        g.setColor(Color.GREEN);
        g.drawString(title, centerX(screenWidth, g, title), screenHeight / 8);

        g.setFont(fontPack.getRegular());
        g.setColor(Color.GRAY);
        g.drawString(instructions, centerX(screenWidth, g, instructions), screenHeight / 5);
    }

    /** high score list */
    public void drawHighScores(final int screenWidth, final int screenHeight, final List<Score> highScores) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);

        int startY = screenHeight / 4;
        int spacing = fontPack.getRegularMetrics().getHeight() * 2;

        int i = 0;
        for (Score s : highScores) {
            String text = String.format("%s        %04d", s.getName(), s.getScore());
            g.drawString(text, centerX(screenWidth, g, text), startY + spacing * (i + 1));
            i++;
        }
    }

    /** achievement list */
    public void drawAchievements(final int screenWidth, final int screenHeight, final List<Achievement> achievements) {
        Graphics g = backBuffer.getGraphics();

        g.setFont(fontPack.getFontBig());
        g.setColor(Color.GREEN);
        g.drawString("Achievements", centerX(screenWidth, g, "Achievements"), screenHeight / 8);

        g.setFont(fontPack.getRegular());

        int startY = screenHeight / 5;
        int spacing = fontPack.getRegularMetrics().getHeight() * 2;

        int i = 0;
        for (Achievement a : achievements) {
            g.setColor(a.isUnlocked() ? Color.GREEN : Color.WHITE);
            String text = a.getName() + " - " + a.getDescription();
            g.drawString(text, centerX(screenWidth, g, text), startY + spacing * (i + 1));
            i++;
        }

        g.setColor(Color.GRAY);
        String backText = "Press ESC to return";
        g.drawString(backText, centerX(screenWidth, g, backText), screenHeight - 50);
    }

    /** Credit title */
    public void drawCreditsMenu(final int screenWidth, final int screenHeight) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontBig());
        g.setColor(Color.GREEN);
        String creditsString = "Credits";
        g.drawString(creditsString,
                (screenWidth - g.getFontMetrics().stringWidth(creditsString)) / 2,
                screenHeight / 8);

        g.setFont(fontPack.getRegular());
        g.setColor(Color.GRAY);
        String instructionsString = "Press Space to return";
        g.drawString(instructionsString,
                (screenWidth - g.getFontMetrics().stringWidth(instructionsString)) / 2,
                screenHeight / 5);
    }

    /** Credit context */
    public void drawCredits(final int screenWidth, final int screenHeight, final java.util.List<String> creditLines) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontSmall());

        int y = screenHeight / 4;
        final int x = screenWidth / 10;
        final int lineSpacing = fontPack.getSmallMetrics().getHeight() + 6;

        for (String line : creditLines) {
            g.setColor(Color.GREEN);
            g.drawString(line, x, y);
            y += lineSpacing;
        }
    }

    /** text center Regular */
    public void drawCenteredRegularString(final int screenWidth, final String text, final int y) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        g.setColor(Color.WHITE);
        int x = (screenWidth - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    /** text center FontBig */
    public void drawCenteredBigString(final int screenWidth, final String text, final int y) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontBig());
        g.setColor(Color.WHITE);
        int x = (screenWidth - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    /**
     * Draws a level countdown ("Level X", "3", "2", "1", "GO!") on the screen.
     */
    public void drawCountDown(final int screenWidth, final int screenHeight, final int level, final int number, final boolean bonusLife) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getFontBig());
        g.setColor(Color.GREEN);

        int rectWidth = screenWidth;
        int rectHeight = screenHeight / 6;
        int rectY = screenHeight / 2 - rectHeight / 2;

        /** background box */
        g.setColor(Color.BLACK);
        g.fillRect(0, rectY, rectWidth, rectHeight);


        String text;
        if (number >= 4) {
            text = bonusLife ? "Level " + level + " - Bonus life!" : "Level " + level;
        } else if (number != 0) {
            text = Integer.toString(number);
        } else {
            text = "GO!";
        }

        FontMetrics metrics = g.getFontMetrics();
        int textX = (screenWidth - metrics.stringWidth(text)) / 2;
        int textY = screenHeight / 2 + metrics.getHeight() / 3;

        g.setColor(Color.GREEN);
        g.drawString(text, textX, textY);
    }


    // center text horizontally
    private int centerX(final int screenWidth, Graphics g, String text) {
        return (screenWidth - g.getFontMetrics().stringWidth(text)) / 2;
    }
}
