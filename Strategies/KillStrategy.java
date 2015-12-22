package scripts.LANChaosKiller.Strategies;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.GroundItems.GroundItems;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

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

            final RSNPC npcs[] = Antiban.orderOfAttack(NPCs.findNearest("Chaos druid"));

            if (npcs == null || npcs.length == 0) {
                // Nothing to attack, idling
                Antiban.doIdleActions();
                return;
            }

            // NPCs to attack are found.
            Antiban.doActivateRun();
            Antiban.waitDelay(true);

            if (Combat.attackNPCs(npcs, true)) {

                if (!Combat.isUnderAttack()) {
                    doLooting();
                    Antiban.doIdleActions();
                }
                Antiban.setIdle(false);
            }
        }
    }

    /**
     * Converts an Arraylist full of Integers into a int[]
     *
     * @param integers
     * @return
     */
    public static int[] buildIntArray(List<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;
        for (Integer n : integers) {
            ints[i++] = n;
        }
        return ints;
    }

    public static void doLooting() {
        ArrayList<Integer> lootIDs = Variables.getInstance().get("lootList", new ArrayList<>());
        ArrayList<Integer> protectIDs = Variables.getInstance().get("protectIds", new ArrayList<>());

        if (lootIDs != null && lootIDs.size() > 0) {

            final int[] ids = buildIntArray(lootIDs);
            final RSGroundItem[] lootItems = GroundItems.find(ids);

            if (lootItems.length > 0) {
                GroundItems.loot(lootItems, 0);
            }
        }

        RSItem[] foodItem = Inventory.find(Variables.getInstance().<String>get("foodName"));
        if (foodItem.length > 0 && !protectIDs.contains(foodItem[0].getID()))
            protectIDs.add(foodItem[0].getID());

        List<Integer> exceptionList = new ArrayList<>(lootIDs);
        exceptionList.addAll(protectIDs);

        // Drop anything except the items we want to loot, our equipment and food.
        Inventory.dropAllExcept(buildIntArray(exceptionList));
    }


    @Override
    public int priority() {
        return 1;
    }
}
