package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import entity.ShopItem;
import entity.DropItem;

/**
 * Manages the display of items in the HUD.
 *
 * Features:
 * - 5 fixed squares for shop items (MultiShot, RapidFire, Penetration, BulletSpeed, ShipSpeed)
 * - 6 dynamic squares for dropped items (Explode, Slow, Stop, Push, Shield, Heal)
 *
 * @author Team 8 - HUD Implementation
 */
public class ItemHUDManager {

    /** Singleton instance */
    private static ItemHUDManager instance;

    /** Size of each item square */
    private static final int ITEM_SQUARE_SIZE = 20;

    /** Spacing between squares */
    private static final int SQUARE_SPACING = 3;

    /** Y position for fixed shop items (bottom row) */
    private static final int FIXED_ITEMS_Y = 450;

    /** Y position for dynamic dropped items (top row) */
    private static final int DYNAMIC_ITEMS_Y = 420;

    /** X position to start drawing items (right side) */
    private int startX;

    /** Currently active dropped items */
    private List<DroppedItemInfo> activeDroppedItems;

    /** Maximum number of dynamic items that can be displayed */
    private static final int MAX_DYNAMIC_ITEMS = 6;

    /** Duration to show dropped items (in milliseconds) */
    private static final long DROPPED_ITEM_DISPLAY_DURATION = 10000; // 10 seconds

    /**
     * Information about a dropped item being displayed
     */
    private static class DroppedItemInfo {
        public DropItem.ItemType itemType;
        public long displayStartTime;

        public DroppedItemInfo(DropItem.ItemType itemType) {
            this.itemType = itemType;
            this.displayStartTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - displayStartTime > DROPPED_ITEM_DISPLAY_DURATION;
        }
    }

    /**
     * Private constructor for singleton pattern
     */
    private ItemHUDManager() {
        this.activeDroppedItems = new ArrayList<>();
    }

    /**
     * Get singleton instance
     */
    public static ItemHUDManager getInstance() {
        if (instance == null) {
            instance = new ItemHUDManager();
        }
        return instance;
    }

    /**
     * Initialize the HUD manager with screen dimensions
     */
    public void initialize(int screenWidth) {
        // Calculate starting X position to align items to the right
        int totalFixedWidth = 5 * ITEM_SQUARE_SIZE + 4 * SQUARE_SPACING;
        this.startX = screenWidth - totalFixedWidth - 20; // 20px margin from right edge
    }

    /**
     * Add a dropped item to be displayed
     */
    public void addDroppedItem(DropItem.ItemType itemType) {
        // Remove expired items first
        cleanupExpiredItems();

        // If we have space, add the new item
        if (activeDroppedItems.size() < MAX_DYNAMIC_ITEMS) {
            activeDroppedItems.add(new DroppedItemInfo(itemType));
        } else {
            // Replace the oldest item
            activeDroppedItems.remove(0);
            activeDroppedItems.add(new DroppedItemInfo(itemType));
        }
    }

    /**
     * Remove expired dropped items
     */
    private void cleanupExpiredItems() {
        activeDroppedItems.removeIf(DroppedItemInfo::isExpired);
    }

    /**
     * Draw all items on the HUD
     */
    public void drawItems(Graphics graphics) {
        cleanupExpiredItems();

        // Draw fixed shop items (bottom row)
        drawFixedShopItems(graphics);

        // Draw dynamic dropped items (top row)
        drawDynamicDroppedItems(graphics);
    }

    /**
     * Draw the 5 fixed shop items
     */
    private void drawFixedShopItems(Graphics graphics) {
        int x = startX;
        int y = FIXED_ITEMS_Y;

        // Shop items in order: MultiShot, RapidFire, Penetration, BulletSpeed, ShipSpeed
        ShopItemType[] shopItems = {
            ShopItemType.MULTI_SHOT,
            ShopItemType.RAPID_FIRE,
            ShopItemType.PENETRATION,
            ShopItemType.BULLET_SPEED,
            ShopItemType.SHIP_SPEED
        };

        for (ShopItemType itemType : shopItems) {
            drawShopItemSquare(graphics, x, y, itemType);
            x += ITEM_SQUARE_SIZE + SQUARE_SPACING;
        }
    }

    /**
     * Draw the 6 dynamic dropped items
     */
    private void drawDynamicDroppedItems(Graphics graphics) {
        int x = startX;
        int y = DYNAMIC_ITEMS_Y;

        // Draw up to 6 dynamic items
        for (int i = 0; i < MAX_DYNAMIC_ITEMS; i++) {
            if (i < activeDroppedItems.size()) {
                DroppedItemInfo itemInfo = activeDroppedItems.get(i);
                drawDroppedItemSquare(graphics, x, y, itemInfo.itemType);
            } else {
                // Draw empty square
                drawEmptySquare(graphics, x, y);
            }
            x += ITEM_SQUARE_SIZE + SQUARE_SPACING;
        }
    }

    /**
     * Draw a shop item square
     */
    private void drawShopItemSquare(Graphics graphics, int x, int y, ShopItemType itemType) {
        boolean isActive = isShopItemActive(itemType);
        int level = getShopItemLevel(itemType);

        // Draw square background
        Color bgColor = isActive ? Color.GREEN : Color.DARK_GRAY;
        graphics.setColor(bgColor);
        graphics.fillRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);

        // Draw border
        graphics.setColor(Color.WHITE);
        graphics.drawRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);

        // Draw item icon/letter
        graphics.setColor(Color.WHITE);
        String itemLetter = getShopItemLetter(itemType);
        graphics.drawString(itemLetter, x + 6, y + 14);

        // Draw level indicator
        if (level > 0) {
            graphics.setColor(Color.YELLOW);
            graphics.drawString(String.valueOf(level), x + ITEM_SQUARE_SIZE - 6, y + ITEM_SQUARE_SIZE - 3);
        }
    }

    /**
     * Draw a dropped item square
     */
    private void drawDroppedItemSquare(Graphics graphics, int x, int y, DropItem.ItemType itemType) {
        // Draw square background
        Color bgColor = getDroppedItemColor(itemType);
        graphics.setColor(bgColor);
        graphics.fillRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);

        // Draw border
        graphics.setColor(Color.WHITE);
        graphics.drawRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);

        // Draw item icon/letter
        graphics.setColor(Color.WHITE);
        String itemLetter = getDroppedItemLetter(itemType);
        graphics.drawString(itemLetter, x + 6, y + 14);
    }

    /**
     * Draw an empty square
     */
    private void drawEmptySquare(Graphics graphics, int x, int y) {
        // Draw empty square background
        graphics.setColor(Color.BLACK);
        graphics.fillRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);

        // Draw border
        graphics.setColor(Color.GRAY);
        graphics.drawRect(x, y, ITEM_SQUARE_SIZE, ITEM_SQUARE_SIZE);
    }

    /**
     * Check if a shop item is active
     */
    private boolean isShopItemActive(ShopItemType itemType) {
        switch (itemType) {
            case MULTI_SHOT:
                return ShopItem.isMultiShotActive();
            case RAPID_FIRE:
                return ShopItem.getRapidFireLevel() > 0;
            case PENETRATION:
                return ShopItem.isPenetrationActive();
            case BULLET_SPEED:
                return ShopItem.getBulletSpeedLevel() > 0;
            case SHIP_SPEED:
                return ShopItem.getSHIPSpeedCOUNT() > 0;
            default:
                return false;
        }
    }

    /**
     * Get the level of a shop item
     */
    private int getShopItemLevel(ShopItemType itemType) {
        switch (itemType) {
            case MULTI_SHOT:
                return ShopItem.getMultiShotLevel();
            case RAPID_FIRE:
                return ShopItem.getRapidFireLevel();
            case PENETRATION:
                return ShopItem.getPenetrationLevel();
            case BULLET_SPEED:
                return ShopItem.getBulletSpeedLevel();
            case SHIP_SPEED:
                return ShopItem.getSHIPSpeedCOUNT();
            default:
                return 0;
        }
    }

    /**
     * Get the letter to display for a shop item
     */
    private String getShopItemLetter(ShopItemType itemType) {
        switch (itemType) {
            case MULTI_SHOT:
                return "M";
            case RAPID_FIRE:
                return "R";
            case PENETRATION:
                return "P";
            case BULLET_SPEED:
                return "B";
            case SHIP_SPEED:
                return "S";
            default:
                return "?";
        }
    }

    /**
     * Get the letter to display for a dropped item
     */
    private String getDroppedItemLetter(DropItem.ItemType itemType) {
        switch (itemType) {
            case Explode:
                return "E";
            case Slow:
                return "L";
            case Stop:
                return "T";
            case Push:
                return "U";
            case Shield:
                return "H";
            case Heal:
                return "A";
            default:
                return "?";
        }
    }

    /**
     * Get the color for a dropped item
     */
    private Color getDroppedItemColor(DropItem.ItemType itemType) {
        switch (itemType) {
            case Explode:
                return Color.RED;
            case Slow:
                return Color.BLUE;
            case Stop:
                return Color.BLUE;
            case Push:
                return Color.BLUE;
            case Shield:
                return Color.CYAN;
            case Heal:
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }

    /**
     * Enum for shop item types
     */
    private enum ShopItemType {
        MULTI_SHOT,
        RAPID_FIRE,
        PENETRATION,
        BULLET_SPEED,
        SHIP_SPEED
    }

}
