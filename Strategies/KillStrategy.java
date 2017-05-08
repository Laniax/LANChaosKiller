package scripts.LANChaosKiller.Strategies;

import org.tribot.api.Clicking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.lanapi.game.filters.Filters;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.grounditems.GroundItems;
import scripts.lanapi.game.helpers.ItemsHelper;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.network.ItemPrice;
import scripts.lanapi.network.exceptions.ItemPriceNotFoundException;

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

        final boolean equipBolts = Vars.get().get("equipBolts", false);
        final boolean lootAbove = Vars.get().get("lootAbove", false);
        final int lootAboveAmount = Vars.get().get("lootAboveAmount", 1);

        List<RSGroundItem> lootList = new ArrayList<>();

        PaintHelper.statusText = "Searching for loot";

        for (RSGroundItem item : GroundItems.getAll(Filters.GroundItems.inArea(Positions.AREA_INSIDE_TOWER))) {

            ItemIDs i = ItemIDs.valueOf(item.getID());

            if (i != null && i.shouldLoot()) {
                lootList.add(item);
                continue;
            }

            if (lootAbove) {

                try {
                    int lootPrice = ItemPrice.get(item.getID());

                    if (lootPrice >= lootAboveAmount) {
                        lootList.add(item);
                        log.info("Looting item id '%d' because it is worth %dgp (threshold: %d).", item.getID(), lootPrice, lootAboveAmount);
                    }
                } catch (ItemPriceNotFoundException e) {
                    log.error("Could not find price for item id %d. We can not add it to the loot list based on value.", e.getItemId());
                }
            }
        }

        if (lootList.size() > 0) {
            PaintHelper.statusText = "Looting items";
            GroundItems.loot(lootList.toArray(new RSGroundItem[lootList.size()]), 0);
        }

        // Equip mithril arrows after looting
        if (equipBolts) {

            RSItem[] bolts = Inventory.find(ItemIDs.MITHRIL_BOLTS.getID());

            if (bolts.length > 0 && bolts[0] != null)
                Clicking.click("Wield", bolts[0]);
        }

        // Lets drop all the stuff thats not on the lootList.. and our food.
        List<RSItem> dropList = new ArrayList<>();

        for (RSItem item : Inventory.getAll()) {

            ItemIDs i = ItemIDs.valueOf(item.getID());
            if (i != null && i.shouldLoot())
                continue;

            String itemName = ItemsHelper.getName(item);
            if (itemName != null && itemName.toLowerCase().equals(Combat.getFoodName().toLowerCase()))
                continue;

            try {
                if (lootAbove && ItemPrice.get(item.getID()) >= lootAboveAmount)
                    continue;
            } catch (ItemPriceNotFoundException e) {
                log.error("Could not find price for item id %d. We can not add it to the drop list based on value.", e.getItemId());
            }

            dropList.add(item);
        }

        if (dropList.size() > 0) {
            PaintHelper.statusText = "Dropping unwanted items";
            Inventory.drop(dropList.toArray(new RSItem[dropList.size()]));
        }
    }


    @Override
    public int priority() {
        return 1;
    }
}
