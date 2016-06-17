package scripts.LANChaosKiller;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.*;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LANChaosKiller.Strategies.*;
import scripts.LANChaosKiller.UI.PaintInfo;
import scripts.lanapi.core.gui.GUI;
import scripts.lanapi.core.logging.LogProxy;
import scripts.lanapi.core.mathematics.FastMath;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.combat.Combat;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.concurrency.Condition;
import scripts.lanapi.game.concurrency.observers.inventory.InventoryObserver;
import scripts.lanapi.game.helpers.ArgumentsHelper;
import scripts.lanapi.game.helpers.ItemsHelper;
import scripts.lanapi.game.helpers.SkillsHelper;
import scripts.lanapi.game.movement.Movement;
import scripts.lanapi.game.painting.AbstractPaintInfo;
import scripts.lanapi.game.painting.PaintHelper;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.AbstractScript;
import scripts.lanapi.network.connectivity.Signature;
import scripts.lanapi.network.ItemPrice;
import scripts.lanapi.network.exceptions.ItemPriceNotFoundException;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * [LAN] Chaos Killer
 * Kills druids in the tower above ardougne for combat exp + herbs.
 *
 * @author Laniax
 */

@ScriptManifest(authors = {"Laniax"}, category = "Combat", name = "[LAN] Chaos Killer", description = "Local script")
public class LANChaosKiller extends AbstractScript implements Painting, EventBlockingOverride, MouseActions, MousePainting, MouseSplinePainting, Ending, Breaking, Arguments, MessageListening07 {

    private LogProxy inventoryLog;

    @Override
    public IStrategy[] getStrategies() {
        return new IStrategy[]{new StuckStrategy(), new BankingStrategy(), new KillStrategy(), new PicklockDoorStrategy(), new TravelToBankStrategy(), new TravelToTowerStrategy(), new WorldhopStrategy()};
    }

    @Override
    public GUI getGUI() {
        try {
            return new GUI(new URL("http://laniax.eu/paint/chaoskiller/gui.fxml"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BufferedImage getNotificationIcon() {
        return PaintHelper.getBufferedImage("http://laniax.eu/paint/tribot.png");
    }

    /**
     * This method is called once when the script starts and we are logged ingame, just before the paint/gui shows.
     */
    @Override
    public void onInitialize() {

        SkillsHelper.setStartSkills(new SKILLS[]{SKILLS.ATTACK, SKILLS.STRENGTH, SKILLS.DEFENCE, SKILLS.HITPOINTS, SKILLS.RANGED, SKILLS.MAGIC});

        boolean useLogCrossing = Skills.getActualLevel(SKILLS.AGILITY) >= 33;

        if (!useLogCrossing)
            log.info("Detected that you are lower then 33 agility. We will walk over the bridge instead of the log.");

        Vars.get().addOrUpdate("script", this);
        Vars.get().addOrUpdate("useLogCrossing", useLogCrossing);

        Combat.setAutoRetaliate(true);

        Movement.setUseCustomDoors(new RSObject[]{});
        Movement.setExcludeTiles(Positions.AREA_INSIDE_TOWER.getAllTiles());

        log.info("Retrieving loot prices..");
        for (ItemIDs item : ItemIDs.values()) {
            try {
                ItemPrice.get(item.getID()); // this is cached
            } catch (ItemPriceNotFoundException e) {
                log.error("Error getting price for %s.", item.name());
            }
        }
        log.info("Got all prices!");

        inventoryLog = new LogProxy("Inventory");

        InventoryObserver observer = new InventoryObserver(new Condition() {
            @Override
            public boolean active() {
                return !Banking.isBankScreenOpen();
            }
        });

        observer.addListener(this);
        observer.start();
    }

    @Override
    public AbstractPaintInfo getPaintInfo() {
        return new PaintInfo();
    }

    @Override
    public void inventoryItemAdded(RSItem item, int count) {

        int itemPrice;
        try {
            itemPrice = ItemPrice.get(item.getID());
        } catch (ItemPriceNotFoundException e) {
            inventoryLog.error("Couldn't find value for item: %s. We are not counting it towards our profit value.", ItemsHelper.getName(item));
            return;
        }

        int totalWorth = itemPrice * count;

        inventoryLog.info("Gained item: %dx %s. (worth: %s | %s / each)", count, ItemsHelper.getName(item), PaintHelper.formatNumber(totalWorth, true), PaintHelper.formatNumber(itemPrice, true));
        PaintHelper.profit += totalWorth;
    }

    @Override
    public void inventoryItemRemoved(RSItem item, int count) {

        int itemPrice;

        try {
            itemPrice = ItemPrice.get(item.getID());
        } catch (ItemPriceNotFoundException e) {
            inventoryLog.error("Couldn't find value for item: %s. We are not counting it towards our profit value.", ItemsHelper.getName(item));
            return;
        }

        int totalWorth = itemPrice * count;

        inventoryLog.info("Lost item: %dx %s. (worth: %s | %s / each)", count, ItemsHelper.getName(item), PaintHelper.formatNumber(totalWorth, true), PaintHelper.formatNumber(itemPrice, true));
        PaintHelper.profit -= totalWorth;
    }

    @Override
    public void passArguments(HashMap<String, String> hashMap) {

        HashMap<String, String> args = ArgumentsHelper.get(hashMap);

        if (args.size() == 0) {
            hasArguments = false;
            return;
        }

        if (log == null)
            log = new LogProxy("Arguments"); // passArguments is called before script#run.

        hasArguments = true;

        String[] options = {"foodname", "foodcount", "equipbolts", "worldhop", "lootabove", "exclude", "notifications"};

        for (String option : options) {

            String value = args.get(option);
            boolean useDefault = value == null;

            switch (option) {

                case "foodname":
                    Combat.setFoodName(useDefault ? "Lobster" : value);
                    continue;

                case "foodcount":
                    if (useDefault) {
                        Vars.get().addOrUpdate("foodCount", 1);
                        continue;
                    }

                    if (isInt(value)) {
                        int val = FastMath.minMax(Integer.valueOf(value), 0, 27);
                        Vars.get().addOrUpdate("foodCount", val);
                        continue;
                    }

                    break;
                case "equipbolts":
                    if (useDefault) {
                        Vars.get().addOrUpdate("equipBolts", true);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Vars.get().addOrUpdate("equipBolts", val);
                        continue;
                    }
                    break;
                case "worldhop":
                    if (useDefault) {
                        Vars.get().addOrUpdate("worldhop", true);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Vars.get().addOrUpdate("worldhop", val);
                        continue;
                    }
                    break;
                case "lootabove":
                    if (useDefault) {
                        Vars.get().addOrUpdate("lootAbove", true);
                        Vars.get().addOrUpdate("lootAboveAmount", 5000);
                        continue;
                    }
                    if (isInt(value)) {
                        int val = Math.max(Integer.valueOf(value), 1);
                        Vars.get().addOrUpdate("lootAbove", true);
                        Vars.get().addOrUpdate("lootAboveAmount", val);
                        continue;
                    }
                    break;
                case "notifications":
                    if (useDefault) {
                        Vars.get().addOrUpdate("enableNotifications", false);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Vars.get().addOrUpdate("enableNotifications", val);
                        continue;
                    }
                    break;
                case "exclude":

                    List<ItemIDs> excludes = new ArrayList<>();

                    if (!useDefault) {
                        String[] vals = value.split(",");

                        for (String v : vals) {
                            ItemIDs item = ItemIDs.valueOf(v);
                            if (item != null)
                                excludes.add(item);
                            else
                                log.error("Error processing loot exclude '%s'.", v);
                        }
                    }

                    for (ItemIDs item : ItemIDs.values()) {

                        item.shouldLoot(!excludes.contains(item));

                        if (excludes.contains(item))
                            log.info("Excluding %s from the loot table.", item.name());

                    }
                    continue;
            }

            log.error("Error processing '%s' for option '%s'! There might be unexpected behaviour!", value, option);
        }
    }

    private boolean isBoolean(String str) {
        return str.toLowerCase().equals("true") || str.toLowerCase().equals("false");
    }

    private boolean isInt(String str) {
        return str.matches("^-?\\d+$");
    }


    /**
     * The url of the server. In the following format:
     * http://yourdomain.com/scripts/yourscriptname/
     *
     * @return
     */
    @Override
    public String signatureServerUrl() {
        return "http://laniax.eu/scripts/Chaos%20Killer/";
    }

    /**
     * Called every 5 minutes to send data to the server. Please send the values -from the script start- and NOT from since the last call.
     *
     * @return A hashmap with String that equals the a Type name on the server, and the integer value of the variable.
     */
    @Override
    public HashMap<String, Integer> signatureSendData() {

        HashMap<String, Integer> result = new HashMap<>();

        result.put("attackXP", SkillsHelper.getReceivedXP(SKILLS.ATTACK, getRunningTime()));
        result.put("strengthXP", SkillsHelper.getReceivedXP(SKILLS.STRENGTH, getRunningTime()));
        result.put("defenceXP", SkillsHelper.getReceivedXP(SKILLS.DEFENCE, getRunningTime()));
        result.put("hitpointsXP", SkillsHelper.getReceivedXP(SKILLS.HITPOINTS, getRunningTime()));
        result.put("rangedXP", SkillsHelper.getReceivedXP(SKILLS.RANGED, getRunningTime()));
        result.put("magicXP", SkillsHelper.getReceivedXP(SKILLS.MAGIC, getRunningTime()));
        result.put("druidsKilled", Antiban.getResourcesWon());
        result.put("profit", PaintHelper.profit);

        return result;
    }
}