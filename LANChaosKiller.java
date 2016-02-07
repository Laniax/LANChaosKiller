package scripts.LANChaosKiller;

import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.*;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LANChaosKiller.Strategies.*;
import scripts.LANChaosKiller.UI.PaintInfo;
import scripts.LanAPI.Core.GUI.GUI;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Core.Mathematics.FastMath;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.ArgumentsHelper;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Persistance.Variables;
import scripts.LanAPI.Game.Script.AbstractScript;
import scripts.LanAPI.Network.Connectivity.Signature;
import scripts.LanAPI.Network.ItemPrice;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * [LAN] Chaos Killer
 * Kills druids in the tower above ardougne for combat exp + herbs.
 *
 * @author Laniax
 */

@ScriptManifest(authors = {"Laniax"}, category = "Combat", name = "[LAN] Chaos Killer")
public class LANChaosKiller extends AbstractScript implements Painting, EventBlockingOverride, MouseActions, MousePainting, Ending, Breaking, Arguments, MessageListening07 {

    @Override
    public IStrategy[] getStrategies() {
        return new IStrategy[]{new StuckStrategy(), new BankingStrategy(), new KillStrategy(), new PicklockDoorStrategy(), new TravelToBankStrategy(), new TravelToTowerStrategy(), new WorldhopStrategy()};
    }

    @Override
    public GUI getGUI() {
        return new GUI(getClass().getResource("UI/gui.fxml"));
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

        Variables.getInstance().addOrUpdate("useLogCrossing", useLogCrossing);

        Combat.setAutoRetaliate(true);

        Movement.setExcludeTiles(Positions.AREA_INSIDE_TOWER.getAllTiles());

        log.info("Retrieving loot prices..");
        for (ItemIDs item : ItemIDs.values()) {

            int price = ItemPrice.get(item.getID()); // this is cached
            if (price == 0)
                log.error("Error getting price for %s", item.name());
        }
        log.info("Got all prices!");
    }

    @Override
    public AbstractPaintInfo getPaintInfo() {
        return new PaintInfo();
    }

    @Override
    public void onEnd() {
        super.onEnd();

        HashMap<String, Integer> vars = new HashMap<>();
        vars.put("attackXP", SkillsHelper.getReceivedXP(SKILLS.ATTACK));
        vars.put("strengthXP", SkillsHelper.getReceivedXP(SKILLS.STRENGTH));
        vars.put("defenceXP", SkillsHelper.getReceivedXP(SKILLS.DEFENCE));
        vars.put("hitpointsXP", SkillsHelper.getReceivedXP(SKILLS.HITPOINTS));
        vars.put("rangedXP", SkillsHelper.getReceivedXP(SKILLS.RANGED));
        vars.put("magicXP", SkillsHelper.getReceivedXP(SKILLS.MAGIC));
        vars.put("druidsKilled", Antiban.getResourcesWon());
        vars.put("profit", PaintHelper.profit);
        if (Signature.send("http://laniax.eu/scripts/Chaos%20Killer/signature/update", this.getRunningTime(), vars))
            log.debug("Succesfully posted signature data.");
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
                        Variables.getInstance().addOrUpdate("foodCount", 1);
                        continue;
                    }

                    if (isInt(value)) {
                        int val = FastMath.minMax(Integer.valueOf(value), 0, 27);
                        Variables.getInstance().addOrUpdate("foodCount", val);
                        continue;
                    }

                    break;
                case "equipbolts":
                    if (useDefault) {
                        Variables.getInstance().addOrUpdate("equipBolts", true);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Variables.getInstance().addOrUpdate("equipBolts", val);
                        continue;
                    }
                    break;
                case "worldhop":
                    if (useDefault) {
                        Variables.getInstance().addOrUpdate("worldhop", true);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Variables.getInstance().addOrUpdate("worldhop", val);
                        continue;
                    }
                    break;
                case "lootabove":
                    if (useDefault) {
                        Variables.getInstance().addOrUpdate("lootAbove", true);
                        Variables.getInstance().addOrUpdate("lootAboveAmount", 5000);
                        continue;
                    }
                    if (isInt(value)) {
                        int val = Math.max(Integer.valueOf(value), 1);
                        Variables.getInstance().addOrUpdate("lootAbove", true);
                        Variables.getInstance().addOrUpdate("lootAboveAmount", val);
                        continue;
                    }
                    break;
                case "notifications":
                    if (useDefault) {
                        Variables.getInstance().addOrUpdate("enableNotifications", false);
                        continue;
                    }
                    if (isBoolean(value)) {
                        boolean val = value.toLowerCase().equals("true");
                        Variables.getInstance().addOrUpdate("enableNotifications", val);
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
}