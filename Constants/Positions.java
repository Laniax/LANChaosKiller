package scripts.LANChaosKiller.Constants;

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
    public static final RSArea AREA_BANK = new RSArea(new RSTile(2613, 3334, 0), new RSTile(2620, 3332, 0));

    public static final int COORD_X_RIVER = 2600;

    public static final RSTile POS_BANK_CENTER = new RSTile(2617, 3332, 0);

    public static final RSTile POS_INTERACT_LOG_BANK = new RSTile(2602, 3336, 0);
    public static final RSTile POS_INTERACT_LOG_TOWER = new RSTile(2598, 3336, 0);

    public static final RSTile POS_OBJ_LOG_BANK = new RSTile(2601, 3336, 0);
    public static final RSTile POS_OBJ_LOG_TOWER = new RSTile(2599, 3336, 0);
    public static final RSTile POS_OUTSIDE_DRUID_TOWER_DOOR = new RSTile(2565, 3356, 0);
    public static final RSTile POS_STAIRS_DOWNSTAIRS_TOWER = new RSTile(2563, 9756);
}
