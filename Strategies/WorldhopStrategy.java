package scripts.LANChaosKiller.Strategies;

import org.tribot.api2007.Players;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.ext.Filters;
import scripts.LANChaosKiller.Constants.Positions;
import scripts.LanAPI.Core.Logging.LogProxy;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Concurrency.IStrategy;
import scripts.LanAPI.Game.Persistance.Variables;

/**
 * @author Laniax
 */
public class WorldhopStrategy implements IStrategy {

    LogProxy log = new LogProxy("WorldhopStrategy");

    @Override
    public boolean isValid() {

        boolean worldhopEnabled = Variables.getInstance().get("worldhop", false);

        if (!worldhopEnabled)
            return false;

        final int competitionCount = Players.getAll(Filters.Players.inArea(Positions.AREA_INSIDE_TOWER)).length - 1;

        return Antiban.switchResources(competitionCount);
    }

    @Override
    public void run() {

        int world = WorldHopper.getRandomWorld(true, false);

        log.info("Attempting to hop to world %d.", world);

        WorldHopper.changeWorld(world);
    }

    @Override
    public int priority() {
        return 15;
    }
}
