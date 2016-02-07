package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Painting.PaintHelper;

/**
 * @author Laniax
 */
public class PicklockDoorStrategy implements IStrategy {

    LogProxy log = new LogProxy("PicklockDoorStrategy");

    @Override
    public boolean isValid() {

        return Player.getPosition().distanceTo(Positions.POS_OUTSIDE_DRUID_TOWER_DOOR) < 5 && !Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());
    }

    @Override
    public void run() {
        PaintHelper.statusText = "Picklocking door";

        if (ObjectsHelper.interact("Pick-lock")) {

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());
                }
            }, General.random(100, 200));

            Antiban.setWaitingSince();
        }
    }

    @Override
    public int priority() {
        return 1;
    }
}
