package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
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
public class TravelToTowerStrategy implements IStrategy {

    LogProxy log = new LogProxy("TravelToTowerStrategy");

    @Override
    public boolean isValid() {

        // picklocking has higher priority so this is okay
        return !Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());

    }

    @Override
    public void run() {

        boolean useLogCrossing = Variables.getInstance().get("useLogCrossing", false);

        if (useLogCrossing && Player.getPosition().getX() > Positions.COORD_X_RIVER) {

            PaintHelper.statusText = "Going to log";

            Walking.walkPath(Positions.PATH_BANK_TO_LOG, new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Player.getPosition().distanceTo(Positions.PATH_BANK_TO_LOG[Positions.PATH_BANK_TO_LOG.length - 1]) < 3;
                }
            }, General.random(18000, 20000));

            PaintHelper.statusText = "Crossing log";

            for (int i = 0; i < 10; i++) {

                ObjectsHelper.interact("Walk-across", Positions.POS_OBJ_LOG_BANK);

                if (Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return Player.getPosition().getX() < Positions.COORD_X_RIVER;
                    }
                }, General.random(2000, 3000)))
                    break;
            }
        }

        PaintHelper.statusText = "Going to tower";

        if (useLogCrossing)
            Walking.walkPath(Positions.PATH_LOG_TO_TOWER);
        else
            Movement.walkTo(Positions.POS_OUTSIDE_DRUID_TOWER_DOOR);
    }

    @Override
    public int priority() {
        return 0;
    }
}
