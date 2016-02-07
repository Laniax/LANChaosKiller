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

//    LogProxy log = new LogProxy("TravelToBankStrategy");

    @Override
    public boolean isValid() {

        boolean needBankingForFood = Inventory.isFull();

        return needBankingForFood && Player.getPosition().distanceTo(Positions.POS_BANK_CENTER) > 2;
    }

    @Override
    public void run() {

        boolean useLogCrossing = Variables.getInstance().get("useLogCrossing", false);

        // We are in the tower, first open the door.
        if (Positions.AREA_INSIDE_TOWER.contains(Player.getPosition())) {

            PaintHelper.statusText = "Opening door";

            ObjectsHelper.interact("Open");

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

            // interact will move to the object if it isnt near
            if (!ObjectsHelper.interact("Walk-across", Positions.POS_OBJ_LOG_TOWER))
                return;
            else {
                if (Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(30);
                        return Player.getPosition().getX() > Positions.COORD_X_RIVER;
                    }
                }, General.random(4000, 5000)));
            }
        }

        PaintHelper.statusText = "Going to bank";

        Movement.walkTo(Positions.POS_BANK_CENTER);
    }

    @Override
    public int priority() {
        return 9;
    }
}
