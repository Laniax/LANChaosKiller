package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ObjectsHelper;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.PaintHelper;

/**
 * @author Laniax
 */
public class StuckStrategy implements IStrategy {

    LogProxy log = new LogProxy("StuckStrategy");

    @Override
    public boolean isValid() {

        return Positions.AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition()) || Positions.AREA_UPSTAIRS_TOWER.contains(Player.getPosition());
    }

    @Override
    public void run() {

        PaintHelper.statusText = "Unstucking";
        // Check if we might be upstairs
        if (Player.getPosition().getPlane() > 0) {

            log.warn("We are upstairs - unstucking");

            ObjectsHelper.interact("Climb-down");

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Player.getPosition().getPlane() == 0;
                }
            }, General.random(3000, 4000));
        } else {
            // otherwise we are downstairs
            log.warn("We are downstairs - unstucking");

            if (!Positions.POS_STAIRS_DOWNSTAIRS_TOWER.isOnScreen())
                Movement.walkTo(Positions.POS_STAIRS_DOWNSTAIRS_TOWER);

            ObjectsHelper.interact("Climb-up");

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return !Positions.AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition());
                }
            }, General.random(3000, 4000));
        }
    }

    @Override
    public int priority() {
        return 11;
    }
}