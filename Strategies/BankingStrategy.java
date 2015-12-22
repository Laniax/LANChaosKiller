package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class BankingStrategy implements IStrategy {

    LogProxy log = new LogProxy("BankingStrategy");

    @Override
    public boolean isValid() {

        boolean needBanking = (Inventory.isFull() || (Variables.getInstance().<Integer>get("foodCount") > 0)) && Inventory.find(Variables.getInstance().<String>get("foodName")).length == 0;

        return needBanking && Player.getPosition().distanceTo(Positions.POS_BANK_CENTER) < 3;
    }

    @Override
    public void run() {

        PaintHelper.statusText = "Banking";

        final int foodCount = Variables.getInstance().get("foodCount");
        final String foodName = Variables.getInstance().get("foodName");

        // OpenBank has issues with a banker not being in reach.
        if (Banking.openBankBooth()) {

            // Pin is handled by Tribot.

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Banking.isBankScreenOpen();
                }
            }, General.random(3000, 4000));


            Banking.depositAll();

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Inventory.getAll().length == 0;
                }
            }, General.random(3000, 4000));

            if (foodCount > 0) {

                Banking.withdraw(foodCount, foodName);

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(50);
                        return Inventory.getCount(foodName) >= foodCount;
                    }
                }, General.random(3000, 4000));
            }

            Banking.close();
        }
    }

    @Override
    public int priority() {
        return 10;
    }
}
