package engine;

import java.awt.*;
import screen.Screen;
import screen.ShopScreen;

public final class ShopRenderer {

    private final BackBuffer backBuffer;
    private final FontPack fontPack;

    public ShopRenderer(BackBuffer backBuffer, FontPack fontPack) {
        this.backBuffer = backBuffer;
        this.fontPack = fontPack;
    }

    public void drawShopScreen(
            final Screen screen,
            final int coinBalance,
            final int selectedItem,
            final int selectionMode,
            final int selectedLevel,
            final int totalItems,
            final String[] itemNames,
            final String[] itemDescriptions,
            final int[][] itemPrices,
            final int[] maxLevels,
            final ShopScreen shopScreen
    ) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        FontMetrics metrics = g.getFontMetrics();

        // --- Title ---
        g.setColor(Color.GREEN);
        drawCenteredString(g, screen, "SHOP", screen.getHeight() / 8, fontPack.getFontBig());

        // --- Coin balance ---
        g.setColor(Color.YELLOW);
        String balanceString = String.format("Your Balance: %d coins", coinBalance);
        drawCenteredString(g, screen, balanceString, 120, fontPack.getRegular());

        // --- Instructions ---
        g.setColor(Color.GRAY);
        String instructions = (selectionMode == 0)
                ? "W/S: Navigate | SPACE: Select | ESC: Exit"
                : "A/D: Change Level | SPACE: Buy | ESC: Back";
        drawCenteredString(g, screen, instructions, 145, fontPack.getRegular());

        // --- Layout ---
        int headerHeight = 165;
        int footerHeight = 50;
        int availableHeight = screen.getHeight() - headerHeight - footerHeight;

        int currentY = 170;
        int baseSpacing = 58;
        int expandedExtraSpace = 55;
        boolean hasExpandedItem = (selectionMode == 1);

        int totalRequiredHeight = totalItems * baseSpacing + (hasExpandedItem ? expandedExtraSpace : 0);
        int adjustedSpacing = Math.max(48, baseSpacing - Math.max(0, totalRequiredHeight - availableHeight) / totalItems);

        // --- item list ---
        for (int i = 0; i < totalItems; i++) {
            boolean isSelected = (i == selectedItem && selectionMode == 0);
            boolean isLevelSelection = (i == selectedItem && selectionMode == 1);
            int currentLevel = shopScreen.getItemCurrentLevel(i);

            drawShopItem(g, screen, itemNames[i], itemDescriptions[i], itemPrices[i],
                    maxLevels[i], currentLevel, currentY,
                    isSelected, coinBalance, isLevelSelection, selectedLevel);

            currentY += adjustedSpacing + (isLevelSelection ? expandedExtraSpace : 0);
        }

        // --- Exit ---
        int exitY = screen.getHeight() - 30;
        g.setColor((selectedItem == totalItems && selectionMode == 0) ? Color.GREEN : Color.WHITE);
        String exitText = shopScreen.betweenLevels ? "< Back to Game >" : "< Back to Main Menu >";
        drawCenteredString(g, screen, exitText, exitY, fontPack.getRegular());
    }

    /**
     * Draws a single shop item with level indicators.
     */
    private void drawShopItem(
            Graphics g,
            Screen screen,
            String itemName,
            String description,
            int[] prices,
            int maxLevel,
            int currentLevel,
            int yPosition,
            boolean isSelected,
            int playerCoins,
            boolean isLevelSelection,
            int selectedLevel
    ) {
        g.setFont(fontPack.getRegular());
        FontMetrics metrics = g.getFontMetrics();

        g.setColor(isSelected || isLevelSelection ? Color.GREEN : Color.WHITE);

        // item name + level info
        String levelInfo = (currentLevel > 0)
                ? String.format(" [Lv.%d/%d]", currentLevel, maxLevel)
                : " [Not Owned]";
        g.drawString(itemName + levelInfo, 30, yPosition);

        // instruction
        if (isSelected || isLevelSelection) {
            g.setColor(Color.GRAY);
            g.drawString(description, 30, yPosition + 15);
        }

        // level chose
        if (isLevelSelection) {
            int levelStartX = 30;
            int currX = levelStartX;
            int currY = yPosition + 35;
            int maxWidth = screen.getWidth() - 60;
            int spaceBetween = 18;

            for (int lvl = 1; lvl <= maxLevel; lvl++) {
                int price = prices[lvl - 1];
                boolean canAfford = playerCoins >= price;
                boolean isOwned = currentLevel >= lvl;
                boolean isThisLevel = (lvl == selectedLevel);

                if (isOwned) g.setColor(Color.DARK_GRAY);
                else if (isThisLevel) g.setColor(Color.GREEN);
                else if (canAfford) g.setColor(Color.WHITE);
                else g.setColor(Color.RED);

                String text = isOwned
                        ? String.format("Lv.%d [OWNED]", lvl)
                        : String.format("Lv.%d (%d$)", lvl, price);

                int textWidth = metrics.stringWidth(text);

                if (currX + textWidth > levelStartX + maxWidth) {
                    currX = levelStartX;
                    currY += metrics.getHeight() + 3;
                }

                g.drawString(text, currX, currY);
                currX += textWidth + spaceBetween;
            }
        }
    }

    /**
     * Draws purchase feedback message (e.g., “Purchased!”, “Not enough coins!”).
     */
    public void drawShopFeedback(final Screen screen, final String message) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());

        int popupWidth = 300;
        int popupHeight = 50;
        int x = screen.getWidth() / 2 - popupWidth / 2;
        int y = 70;

        // translucent background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x, y, popupWidth, popupHeight, 15, 15);

        // text color
        if (message.toLowerCase().contains("purchased")) {
            g.setColor(Color.GREEN);
        } else if (message.toLowerCase().contains("not enough") || message.toLowerCase().contains("failed")) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.YELLOW);
        }

        g.drawRoundRect(x, y, popupWidth, popupHeight, 15, 15);

        // text center
        drawCenteredString(g, screen, message, y + popupHeight / 2 + 5, fontPack.getRegular());
    }

    private void drawCenteredString(Graphics g, Screen screen, String text, int y, Font font) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int x = (screen.getWidth() - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
