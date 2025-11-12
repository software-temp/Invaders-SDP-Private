package engine.DTO;

public class ShopInfoDTO {
    private final int screenWidth;
    private final int screenHeight;
    private final int coinBalance;
    private final int selectedItem;
    private final int selectionMode;
    private final int selectedLevel;
    private final int totalItems;
    private final String[] itemNames;
    private final String[] itemDescriptions;
    private final int[][] itemPrices;
    private final int[] maxLevels;
    private final int[] currentLevels; // ✅ 각 아이템의 현재 레벨 추가
    private final boolean betweenLevels;

    public ShopInfoDTO(
            int screenWidth,
            int screenHeight,
            int coinBalance,
            int selectedItem,
            int selectionMode,
            int selectedLevel,
            int totalItems,
            String[] itemNames,
            String[] itemDescriptions,
            int[][] itemPrices,
            int[] maxLevels,
            int[] currentLevels,
            boolean betweenLevels
    ) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.coinBalance = coinBalance;
        this.selectedItem = selectedItem;
        this.selectionMode = selectionMode;
        this.selectedLevel = selectedLevel;
        this.totalItems = totalItems;
        this.itemNames = itemNames;
        this.itemDescriptions = itemDescriptions;
        this.itemPrices = itemPrices;
        this.maxLevels = maxLevels;
        this.currentLevels = currentLevels;
        this.betweenLevels = betweenLevels;
    }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public int getCoinBalance() { return coinBalance; }
    public int getSelectedItem() { return selectedItem; }
    public int getSelectionMode() { return selectionMode; }
    public int getSelectedLevel() { return selectedLevel; }
    public int getTotalItems() { return totalItems; }
    public String[] getItemNames() { return itemNames; }
    public String[] getItemDescriptions() { return itemDescriptions; }
    public int[][] getItemPrices() { return itemPrices; }
    public int[] getMaxLevels() { return maxLevels; }
    public int[] getCurrentLevels() { return currentLevels; }
    public boolean isBetweenLevels() { return betweenLevels; }
}
