package entity;

import java.util.HashSet;
import java.util.Set;
import entity.DropItem.ItemType;

/**
 * Implements a pool of recyclable items.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class ItemPool {

    /** Set of already created items. */
    private static Set<DropItem> pool = new HashSet<DropItem>();

    /**
     * Constructor, not called.
     */
    private ItemPool() {

    }

    /**
     * Returns a item from the pool if one is available, a new one if there
     * isn't.
     *
     * @param positionX
     *            Requested position of the item in the X axis.
     * @param positionY
     *            Requested position of the item in the Y axis.
     * @param speed
     *            Requested speed of the item, positive or negative depending
     *            on direction - positive is down.
     * @param itemType
     *      * Requested item type.
     * @return Requested item.
     */
    public static DropItem getItem(final int positionX,
                                   final int positionY, final int speed, final ItemType itemType) {
        DropItem dropItem;
        if (!pool.isEmpty()) {
            dropItem = pool.iterator().next();
            pool.remove(dropItem);
            dropItem.setPositionX(positionX - dropItem.getWidth() / 2);
            dropItem.setPositionY(positionY);
            dropItem.setSpeed(speed);
            dropItem.setItemType(itemType);
        } else {
            dropItem = new DropItem(positionX, positionY, speed, itemType);
            dropItem.setPositionX(positionX - dropItem.getWidth() / 2);
        }
        return dropItem;
    }

    /**
     * Adds one or more items to the list of available ones.
     *
     * @param dropItem
     *            items to recycle.
     */
    public static void recycle(final Set<DropItem> dropItem) {
        pool.addAll(dropItem);
    }
}
