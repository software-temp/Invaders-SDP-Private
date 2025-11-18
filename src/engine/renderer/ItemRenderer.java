package engine.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import engine.BackBuffer;
import engine.DrawManager;
import engine.DrawManager.SpriteType;
import entity.DropItem;

public class ItemRenderer {

	private final BackBuffer backBuffer;
	private final Map<SpriteType, boolean[][]> spriteMap;
	private final double scale;

	public ItemRenderer(BackBuffer backBuffer, Map<SpriteType, boolean[][]> spriteMap, double scale) {
		this.backBuffer = backBuffer;
		this.spriteMap = spriteMap;
		this.scale = scale;
	}

	public void render(DropItem item) {
		SpriteType sprite = getSprite(item.getItemType());
		Color color = getColor(item.getItemType());

		boolean[][] image = spriteMap.get(sprite);
		Graphics g = backBuffer.getGraphics();
		g.setColor(color);

		int posX = item.getPositionX();
		int posY = item.getPositionY();

		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				if (image[i][j]) {
					int pixelSize = (int) Math.max(1, 2 * scale);
					int scaledX = posX + (int)(i * pixelSize);
					int scaledY = posY + (int)(j * pixelSize);
					g.fillRect(scaledX, scaledY, pixelSize, pixelSize);
				}
			}
		}
	}

	private SpriteType getSprite(DropItem.ItemType type) {
		switch (type) {
			case Explode: return SpriteType.Item_Explode;
			case Slow:    return SpriteType.Item_Slow;
			case Stop:    return SpriteType.Item_Stop;
			case Push:    return SpriteType.Item_Push;
			case Shield:  return SpriteType.Item_Shield;
			case Heal:    return SpriteType.Item_Heal;
		}
		throw new IllegalArgumentException("Unknown ItemType: " + type);
	}

	private Color getColor(DropItem.ItemType type) {
		switch (type) {
			case Explode: return Color.RED;
			case Slow:    return Color.BLUE;
			case Stop:    return Color.YELLOW;
			case Push:    return Color.ORANGE;
			case Shield:  return Color.CYAN;
			case Heal:    return Color.GREEN;
		}
		throw new IllegalArgumentException("Unknown ItemType: " + type);
	}
}
