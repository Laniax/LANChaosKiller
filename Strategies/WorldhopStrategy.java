package scripts.LANChaosKiller.Strategies;

import org.tribot.api2007.Players;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.ext.Filters;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanframework.logging.Log;
import scripts.lanframework.logging.annotations.LogName;

/**
 * @author Laniax
 */
@LogName("Worldhopper")
public class WorldhopStrategy implements IStrategy {

    @Override
    public boolean isValid() {

        boolean worldhopEnabled = Vars.get().get("worldhop", false);

        if (!worldhopEnabled)
            return false;

        final int competitionCount = Players.getAll(Filters.Players.inArea(Positions.AREA_INSIDE_TOWER)).length - 1;

        return Antiban.switchResources(competitionCount);
    }

    @Override
    public void run() {

        int world = WorldHopper.getRandomWorld(true, false);

        Log.Instance.warn("Attempting to hop to world %d.", world);

        WorldHopper.changeWorld(world);
    }

    @Override
    public int priority() {
        return 15;
    }
}
