package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.Traverse;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.helpers.ObjectsHelper;
import scripts.lanapi.game.painting.PaintHelper;

/**
 * @author Laniax
 */
public class TravelToTowerStrategy implements IStrategy {

//    LogProxy log = new LogProxy("TravelToTowerStrategy");

    @Override
    public boolean isValid() {

        // picklocking has higher priority so this is okay
        return !Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());

    }

    @Override
    public void run() {

        boolean useLogCrossing = Skills.getActualLevel(Skills.SKILLS.AGILITY) >= 33;

        if (useLogCrossing && Player.getPosition().getX() > Positions.COORD_X_RIVER) {

                if (Traverse.Instance.getWalker().to(Positions.POS_INTERACT_LOG_BANK)) {

                    if (ObjectsHelper.interact(Positions.POS_OBJ_LOG_BANK, "Walk-across", "Walk-across")) {

                        PaintHelper.status_text = "Waiting until crossover";

                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(50);
                                return Player.getPosition() == Positions.POS_INTERACT_LOG_TOWER || Player.getPosition().getX() < Positions.COORD_X_RIVER;
                            }
                        }, General.random(4000, 5000));

                        PaintHelper.status_text = "Walking to the tower";

                        // we want to walk either way, if the above condition failed or not
                        Traverse.Instance.getWalker().to(Positions.POS_OUTSIDE_DRUID_TOWER_DOOR);

                    }
                }
        } else {
            PaintHelper.status_text = "Walking to the tower";
            Traverse.Instance.getWalker().to(Positions.POS_OUTSIDE_DRUID_TOWER_DOOR);
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
