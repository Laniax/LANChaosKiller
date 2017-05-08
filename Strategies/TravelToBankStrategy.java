package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.helpers.ObjectsHelper;
import scripts.lanapi.game.movement.Movement;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;

/**
 * @author Laniax
 */
public class TravelToBankStrategy implements IStrategy {

    LogProxy log = new LogProxy("TravelToBankStrategy");

    @Override
    public boolean isValid() {

        final int foodCount = Vars.get().get("foodCount");
        final String foodName = Combat.getFoodName();

        boolean needBankingForFood = Inventory.isFull() || (foodCount > 0 && Inventory.getCount(foodName) == 0);

        return needBankingForFood && Player.getPosition().distanceTo(Positions.POS_BANK_CENTER) > 2;
    }

    @Override
    public void run() {

        // We are in the tower, first open the door.
        if (Positions.AREA_INSIDE_TOWER.contains(Player.getPosition())) {

            PaintHelper.statusText = "Opening door";

            ObjectsHelper.interact("Open");

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return !Positions.AREA_INSIDE_TOWER.contains(Player.getPosition());
                }
            }, General.random(2000, 3000));
        }

        boolean useLogCrossing = Vars.get().get("useLogCrossing", false);

        if (useLogCrossing && Player.getPosition().getX() < Positions.COORD_X_RIVER) {

                PaintHelper.statusText = "Going to log";

                if (Movement.walkTo(Positions.POS_INTERACT_LOG_TOWER)) {

                    PaintHelper.statusText = "Interacting with log";

                    if (ObjectsHelper.interact(Positions.POS_OBJ_LOG_TOWER, "Walk-across", "Walk-across")) {

                        PaintHelper.statusText = "Waiting until crossover";

                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                General.sleep(50);
                                return Player.getPosition() == Positions.POS_INTERACT_LOG_BANK || Player.getPosition().getX() > Positions.COORD_X_RIVER;
                            }
                        }, General.random(6000, 7000));

                        PaintHelper.statusText = "Walking to the bank";

                        // we want to walk either way, if the above condition failed or not
                           Movement.walkTo(Positions.AREA_BANK.getRandomTile());

                        }
                    }
        } else {
            PaintHelper.statusText = "Walking to the bank";
            Movement.walkTo(Positions.AREA_BANK.getRandomTile());
        }
    }

    @Override
    public int priority() {
        return 9;
    }
}
