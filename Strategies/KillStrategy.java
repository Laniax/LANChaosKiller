package scripts.LANChaosKiller.Strategies;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Filters.Filters;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.GroundItems.GroundItems;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;
import scripts.LanAPI.Network.ItemPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laniax
 */
public class KillStrategy implements IStrategy {

    LogProxy log = new LogProxy("KillStrategy");

    @Override
    public boolean isValid() {

        return Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());
    }

    @Override
    public void run() {
        if (!Combat.isUnderAttack()) {

            doLooting();

            PaintHelper.statusText = "Searching for druids";

            final RSNPC npcs[] = NPCs.findNearest("Chaos druid");

            if (npcs.length == 0) {
                // Nothing to attack, idling
                Antiban.doIdleActions();
                return;
            }

            // NPCs to attack are found.
            Antiban.activateRun();

            RSNPC npc = Antiban.determineNextTarget(npcs);

            if (npc != null) {
                Combat.attackNPC(npc);
            }
        } else {

            if (Antiban.get().shouldLeaveGame())
                Antiban.get().leaveGame();
        }
    }

    public void doLooting() {

        final boolean equipBolts = Variables.getInstance().get("equipBolts", false);
        final boolean lootAbove = Variables.getInstance().get("lootAbove", false);
        final int lootAboveAmount = Variables.getInstance().get("lootAboveAmount", 1);

        List<RSGroundItem> lootList = new ArrayList<>();

        for (RSGroundItem item : GroundItems.getAll(Filters.GroundItems.inArea(Positions.AREA_INSIDE_TOWER))) {

            ItemIDs i = ItemIDs.valueOf(item.getID());

            if (i != null && i.shouldLoot()) {
                lootList.add(item);
                continue;
            }

            if (lootAbove) {

                int lootPrice = ItemPrice.get(item.getID());

                if (lootPrice >= lootAboveAmount) {
                    lootList.add(item);
                    log.info("Looting item id '%d' because it is worth %dgp (threshold: %d).", item.getID(), lootPrice, lootAboveAmount);
                }
            }
        }

        if (lootList.size() > 0) {
            GroundItems.loot(lootList.toArray(new RSGroundItem[lootList.size()]), 0);
        }

        // Equip mithril arrows after looting
        if (equipBolts) {

            RSItem[] bolts = Inventory.find(ItemIDs.MITHRIL_BOLTS.getID());

            if (bolts.length > 0 && bolts[0] != null)
                Clicking.click("Wield", bolts[0]);
        }
    }


    @Override
    public int priority() {
        return 1;
    }
}
