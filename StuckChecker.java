package scripts.LANChaosKiller;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;

import scripts.LanAPI.Movement;
import scripts.LanAPI.Objects;
import scripts.LanAPI.Paint;

/***
 * Asynchronously (from the script thread) checks if we are stuck.
 * Since *if* we are stuck, the script thread does nothing and thus we can safely take control in this thread.
 */

class StuckChecker implements Runnable {

	@Override
	public void run() {
		
		while (!LANChaosKiller.quitting) {

			// Check if we might be upstairs
			if (Player.getPosition().getPlane() > 0) {
				
				Paint.statusText = "Unstucking";
				General.println("We are upstairs - unstucking");
				
				Objects.interact("Climb-down");
				
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return LANChaosKiller.AREA_INSIDE_TOWER.contains(Player.getPosition());
					}}, General.random(3000, 4000));
			}

			// Check if we are downstairs
			if (LANChaosKiller.AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition())) {
				
				Paint.statusText = "Unstucking";
				General.println("We are downstairs - unstucking");

				Movement.walkTo(LANChaosKiller.POS_STAIRS_DOWNSTAIRS_TOWER);
				
				Objects.interact("Climb-up");

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return !LANChaosKiller.AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition());
					}}, General.random(3000, 4000));
			}

			General.sleep(500);
		}
	}
}