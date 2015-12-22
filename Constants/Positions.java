package scripts.LANChaosKiller.Constants;

import org.tribot.api.General;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 */
public class Positions {

    //todo: better implementation for shared variables?
    public static final RSArea AREA_INSIDE_TOWER = new RSArea(new RSTile(2562, 3356, 0), 2);
    public static final RSArea AREA_DOWNSTAIRS_TOWER = new RSArea(new RSTile(2561, 9757), new RSTile(2592, 9730));
    public static final RSArea AREA_UPSTAIRS_TOWER = new RSArea(new RSTile(2562, 3356, 1), 2);

    public static final int COORD_X_RIVER = 2600;

    public static final RSTile POS_BANK_CENTER = new RSTile(2617, 3332, 0);
    public static final RSTile POS_OBJ_LOG_BANK = new RSTile(2601, 3336, 0);
    public static final RSTile POS_OBJ_LOG_TOWER = new RSTile(2599, 3336, 0);
    public static final RSTile POS_OUTSIDE_DRUID_TOWER_DOOR = new RSTile(2565, 3356, 0);
    public static final RSTile POS_STAIRS_DOWNSTAIRS_TOWER = new RSTile(2563, 9756);
    public static final RSTile[] PATH_TOWER_TO_LOG = new RSTile[]{
            new RSTile(2566, 3356, 0), new RSTile(2573, 3355, 0),
            new RSTile(2581, 3347, 0), new RSTile(2588, 3340, 0),
            new RSTile(2595, 3339, 0), new RSTile(2597, 3336, 0)
    };

    public static final RSTile[] PATH_BANK_TO_LOG = Walking.randomizePath(new RSTile[]{
            new RSTile(2616, 3332, 0),
            new RSTile(2613, 3339, 0),
            new RSTile(2608, 3332, 0),
            new RSTile(2604, 3333, 0),
            new RSTile(2602, 3336, 0)
    }, General.random(0, 3), General.random(0, 3));

    public static final RSTile[] PATH_LOG_TO_BANK = Walking.invertPath(PATH_BANK_TO_LOG);
    public static final RSTile[] PATH_LOG_TO_TOWER = Walking.invertPath(PATH_TOWER_TO_LOG);
}
