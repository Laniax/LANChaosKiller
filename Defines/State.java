package scripts.LANChaosKiller.Defines;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;

import scripts.LANChaosKiller.LANChaosKiller;
import scripts.LanAPI.Combat;

/**
 * @author Laniax
 *
 */

public enum State {
	GO_TO_BANK {
		@Override
		public void run() { LANChaosKiller.goToBank(); }
	},
	BANKING {
		@Override
		public void run() { LANChaosKiller.doBanking(); }
	},
	GO_TO_DRUIDS {
		@Override
		public void run() { LANChaosKiller.goToDruids(); }
	},
	PICKLOCK_DOOR {
		@Override
		public void run() { LANChaosKiller.doPicklockDoor(); }
	},
	KILL_DRUIDS {
		@Override
		public void run() { LANChaosKiller.doKillDruids(); }
	};

	public abstract void run();

	public static State getState() {
		
		Combat.checkAndEat(LANChaosKiller.foodName);

		if ((Inventory.isFull() || (LANChaosKiller.foodCount > 0)) && Inventory.find(LANChaosKiller.foodName).length == 0) {

			if (Player.getPosition().distanceTo(LANChaosKiller.POS_BANK_CENTER) < 3) {
				// We are at the bank and in need of some banking action.
				return State.BANKING;
			}

			// Inventory is full, or food is gone.. we should move to bank.
			return State.GO_TO_BANK;
		}

		if (Player.getPosition().distanceTo(LANChaosKiller.POS_DRUID_TOWER_CENTER) < 3) {
			// We are at the druids (in the tower).
			return State.KILL_DRUIDS;
		} else if (Player.getPosition().distanceTo(LANChaosKiller.POS_OUTSIDE_DRUID_TOWER_DOOR) < 3) {
			// We are near the tower and inventory is a go!
			return State.PICKLOCK_DOOR;
		}

		// We are not at the tower and have free space in inventory, so lets go to the druids and fill it up!
		return State.GO_TO_DRUIDS;
	}
}