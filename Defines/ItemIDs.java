package scripts.LANChaosKiller.Defines;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Laniax
 * 
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
	MITHRIL_BOLTS(9142);

	private final int id;
	ItemIDs(int id) { this.id = id; }
	public int getID() { return id; }

	private static Map<Integer, ItemIDs> map = new HashMap<Integer, ItemIDs>();

	static {
		for (ItemIDs id : ItemIDs.values()) {
			map.put(id.getID(), id);
		}
	}

	public static ItemIDs valueOf(int itemID) {
		return map.get(itemID);
	}
}