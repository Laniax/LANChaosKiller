package scripts.LANChaosKiller.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 */
public enum ItemIDs {
    // Herbs
    GUAM_LEAF(199),
    MARRENTILL(201),
    TARROMIN(203),
    HARRALANDER(205),
    RANARR(207),
    IRIT(209),
    AVANTOE(211),
    KWUARM(213),
    CADANTINE(215),
    DWARF_WEED(217),
    LANTADYME(2485),

    // Misc
    LAW_RUNE(563),
    NATURE_RUNE(561),
    RUNE_JAVELIN(830),
    MITHRIL_BOLTS(9142),

    // Rare drop list
    SHIELD_LEFT_HALF(2366),
    HALF_KEY_TOOTH(985),
    HALF_KEY_LOOP(987),
    DRAGONSTONE(1615),
    DRAGON_SPEAR(1249),
    ALL_RARES(-1);

    private final int id;

    ItemIDs(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    private static Map<Integer, ItemIDs> map = new HashMap<Integer, ItemIDs>();

    static {
        for (ItemIDs item : ItemIDs.values()) {
            map.put(item.getID(), item);
        }
    }

    public static ItemIDs valueOf(int itemID) {
        return map.get(itemID);
    }
}