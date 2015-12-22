package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class TravelToBankStrategy implements IStrategy {

    LogProxy log = new LogProxy("TravelToBankStrategy");

    @Override
    public boolean isValid() {

        boolean needBankingForFood = (Inventory.isFull() || (Variables.getInstance().<Integer>get("foodCount") > 0)) && Inventory.find(Variables.getInstance().<String>get("foodName")).length == 0;

        return needBankingForFood && Player.getPosition().distanceTo(Positions.POS_BANK_CENTER) > 2;
    }

    @Override
    public void run() {

        boolean useLogCrossing = Variables.getInstance().get("useLogCrossing", false);

        // We are in the tower, first open the door.
        if (Positions.AREA_INSIDE_TOWER.contains(Player.getPosition())) {

            PaintHelper.statusText = "Opening door";

            ObjectsHelper.interact("Open");

            // if it doesn't break early, we recurse call it again.
            if (!Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return !Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());
                }
            }, General.random(2000, 3000)))
                return;
        }

        // if we are left from river and should use the log crossing
        if (useLogCrossing && Player.getPosition().getX() < Positions.COORD_X_RIVER) {

            PaintHelper.statusText = "Going to log";

            Walking.walkPath(Positions.PATH_TOWER_TO_LOG, new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Player.getPosition().distanceTo(Positions.PATH_TOWER_TO_LOG[Positions.PATH_TOWER_TO_LOG.length - 1]) < 3;
                }
            }, General.random(18000, 20000));

            PaintHelper.statusText = "Crossing log";

            for (int i = 0; i < 10; i++) {

                ObjectsHelper.interact("Walk-across", Positions.POS_OBJ_LOG_TOWER);

                if (Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return Player.getPosition().getX() > Positions.COORD_X_RIVER;
                    }}, General.random(2000, 3000)))
                    break;
            }

        }

        PaintHelper.statusText = "Going to bank";

        if (useLogCrossing) {
            if (!Walking.walkPath(Positions.PATH_LOG_TO_BANK)) {
                Movement.walkTo(Positions.POS_BANK_CENTER);
            }
        }
        else
            Movement.walkTo(Positions.POS_BANK_CENTER);
    }

    @Override
    public int priority() {
        return 9;
    }
}
