package scripts.LANChaosKiller.Strategies;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Player;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.lanapi.core.system.Notifications;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.inventory.Inventory;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;

/**
 * @author Laniax
 */
public class BankingStrategy implements IStrategy {

//    LogProxy log = new LogProxy("BankingStrategy");

    @Override
    public boolean isValid() {

        final int foodCount = Vars.get().get("foodCount");
        final String foodName = Combat.getFoodName();

        boolean needBankingForFood = Inventory.isFull() || (foodCount > 0 && Inventory.getCount(foodName) == 0);

        return needBankingForFood && Player.getPosition().distanceTo(Positions.POS_BANK_CENTER) < 3;
    }

    @Override
    public void run() {

        PaintHelper.statusText = "Banking";

        final int foodCount = Vars.get().get("foodCount");
        final String foodName = Combat.getFoodName();

        final boolean bankingNotification = Vars.get().get("bankingNotification", false);

        if (bankingNotification)
            Notifications.send("[LAN] ChaosKiller", "Banking..");

        if (Banking.openBank()) {

            // Pin is handled by Tribot.

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(50);
                    return Banking.isBankScreenOpen();
                }
            }, General.random(3000, 4000));


            if (!Inventory.isEmpty())
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
            Antiban.setWaitingSince();
        }
    }

    @Override
    public int priority() {
        return 10;
    }
}
