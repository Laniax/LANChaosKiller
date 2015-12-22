package scripts.LANChaosKiller;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;
import scripts.LANChaosKiller.Strategies.*;
import scripts.LANChaosKiller.UI.GUI;
import scripts.LANChaosKiller.UI.PaintInfo;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Movement.Movement;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Persistance.Variables;
import scripts.LanAPI.Game.Script.AbstractScript;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [LAN] Chaos Killer
 * Kills druids in the tower above ardougne for combat exp + herbs.
 * Supports tribot's ABC system. (Score 10)
 *
 * @author Laniax
 */

@ScriptManifest(authors = {"Laniax"}, category = "Combat", name = "[LAN] Chaos Killer")
public class LANChaosKiller extends AbstractScript implements Painting, EventBlockingOverride, MouseActions, MousePainting {

    @Override
    public IStrategy[] getStrategies() {
        return new IStrategy[]{new StuckStrategy(), new BankingStrategy(), new KillStrategy(), new PicklockDoorStrategy(), new TravelToBankStrategy(), new TravelToTowerStrategy()};
    }

    @Override
    public JFrame getGUI() {
        return new GUI();
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

        // Tell the movement classes not to auto-find/open doors.
        // The only door we encounter is scripted due to pick-lock.
        Movement.setUseCustomDoors(true, new RSObject[]{});

        Combat.setAutoRetaliate(true);

        // protect our gear when dropping.
        List<Integer> protectIDs = new ArrayList<>();
        for (RSItem item : Equipment.getItems()) {
            protectIDs.add(item.getID());
        }

        Variables.getInstance().addOrUpdate("protectIds", protectIDs);
    }

    @Override
    public AbstractPaintInfo getPaintInfo() {
        return new PaintInfo();
    }

}