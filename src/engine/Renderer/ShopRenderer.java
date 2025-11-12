package engine.Renderer;

import engine.BackBuffer;
import engine.DTO.ShopInfoDTO;
import engine.FontPack;

import java.awt.*;

public final class ShopRenderer {

    private final BackBuffer backBuffer;
    private final FontPack fontPack;

    public ShopRenderer(BackBuffer backBuffer, FontPack fontPack) {
        this.backBuffer = backBuffer;
        this.fontPack = fontPack;
    }

    public void drawShopScreen(final ShopInfoDTO dto)
    {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());
        FontMetrics metrics = g.getFontMetrics();

        // --- Title ---
        g.setColor(Color.GREEN);
        drawCenteredString(dto.getScreenWidth(), "SHOP", dto.getScreenHeight() / 8, fontPack.getFontBig());

        // --- Coin balance ---
        g.setColor(Color.YELLOW);
        String balanceString = String.format("Your Balance: %d coins", dto.getCoinBalance());
        drawCenteredString(dto.getScreenWidth(), balanceString, 120, fontPack.getRegular());

        // --- Instructions ---
        g.setColor(Color.GRAY);
        String instructions = (dto.getSelectionMode() == 0)
                ? "W/S: Navigate | SPACE: Select | ESC: Exit"
                : "A/D: Change Level | SPACE: Buy | ESC: Back";
        drawCenteredString(dto.getScreenWidth(), instructions, 145, fontPack.getRegular());

        // --- Layout ---
        int headerHeight = 165;
        int footerHeight = 50;
        int availableHeight = dto.getScreenHeight() - headerHeight - footerHeight;

        int currentY = 170;
        int baseSpacing = 58;
        int expandedExtraSpace = 55;
        boolean hasExpandedItem = (dto.getSelectionMode() == 1);

        int totalRequiredHeight = dto.getTotalItems() * baseSpacing + (hasExpandedItem ? expandedExtraSpace : 0);
        int adjustedSpacing = Math.max(48, baseSpacing - Math.max(0, totalRequiredHeight - availableHeight) / dto.getTotalItems());

        // --- item list ---
        for (int i = 0; i < dto.getTotalItems(); i++) {
            boolean isSelected = (i == dto.getSelectedItem() && dto.getSelectionMode() == 0);
            boolean isLevelSelection = (i == dto.getSelectedItem() && dto.getSelectionMode() == 1);
            int currentLevel = dto.getCurrentLevels()[i];

            drawShopItem(dto.getScreenWidth(), dto.getItemNames()[i], dto.getItemDescriptions()[i], dto.getItemPrices()[i],
                    dto.getMaxLevels()[i], currentLevel, currentY,
                    isSelected, dto.getCoinBalance(), isLevelSelection, dto.getSelectedLevel());

            currentY += adjustedSpacing + (isLevelSelection ? expandedExtraSpace : 0);
        }

        // --- Exit ---
        int exitY = dto.getScreenHeight() - 30;
        g.setColor((dto.getSelectedItem() == dto.getTotalItems() && dto.getSelectionMode() == 0) ? Color.GREEN : Color.WHITE);
        String exitText = dto.isBetweenLevels() ? "< Back to Game >" : "< Back to Main Menu >";
        drawCenteredString(dto.getScreenWidth(), exitText, exitY, fontPack.getRegular());
    }

    /**
     * Draws a single shop item with level indicators.
     */
    private void drawShopItem(
            final int screenWidth,
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
        Graphics g = backBuffer.getGraphics();
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
            int maxWidth = screenWidth - 60;
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
    public void drawShopFeedback(final int screenWidth, final String message) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(fontPack.getRegular());

        int popupWidth = 300;
        int popupHeight = 50;
        int x = screenWidth / 2 - popupWidth / 2;
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
        drawCenteredString(screenWidth, message, y + popupHeight / 2 + 5, fontPack.getRegular());
    }

    private void drawCenteredString(final int screenWidth, String text, int y, Font font) {
        Graphics g = backBuffer.getGraphics();
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int x = (screenWidth - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
