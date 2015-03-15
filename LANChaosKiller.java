package scripts.LANChaosKiller;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;

import scripts.LANChaosKiller.Defines.State;
import scripts.LanAPI.Antiban;
import scripts.LanAPI.Combat;
import scripts.LanAPI.Looting;
import scripts.LanAPI.Movement;
import scripts.LanAPI.Objects;
import scripts.LanAPI.Paint;
import scripts.LanAPI.Skills;

/**
 * [LAN] Chaos Killer
 *   Kills druids in the tower above ardougne for combat exp + herbs.
 *   Supports tribot's ABC system. (Score 10)
 *   
 * @author Laniax
 */

@ScriptManifest(authors = { "Laniax" }, category = "Combat", name = "[LAN] Chaos Killer")
public class LANChaosKiller extends Script implements Painting, EventBlockingOverride, MouseActions, MousePainting {

	public static String statusText = "Starting..";
	public static String foodName = "Lobster";

	public static boolean quitting = false;
	public static boolean waitForGUI = true;
	public static boolean isDoingRandom = false;
	private static boolean useLogCrossing = true;
	private static boolean wasIdle = false;
	private static long idleSince = 0;
	public static boolean shouldEat = false;
	public static int foodCount = 0;

	public static ArrayList<Integer> lootIDs = new ArrayList<Integer>();
	public static ArrayList<Integer> protectIDs = new ArrayList<Integer>();

	public static final RSTile POS_STAIRS_DOWNSTAIRS_TOWER = new RSTile(2563, 9756);
	public static final RSArea AREA_DOWNSTAIRS_TOWER = new RSArea(new RSTile(2561, 9757), new RSTile(2592, 9730));
	public static final RSTile POS_OUTSIDE_DRUID_TOWER_DOOR = new RSTile(2565, 3356, 0);
	public static final RSTile POS_DRUID_TOWER_CENTER = new RSTile(2562, 3356, 0);
	public static final RSTile POS_BANK_CENTER = new RSTile(2617, 3332, 0);
	private static final int COORD_X_RIVER = 2600;

	private static final RSTile[] PATH_TOWER_TO_LOG = new RSTile[] {
		new RSTile(2566, 3356, 0), new RSTile(2573, 3355, 0), 
		new RSTile(2581, 3347, 0), new RSTile(2588, 3340, 0), 
		new RSTile(2595, 3339, 0), new RSTile(2597, 3336, 0)
	};

	private static final RSTile[] PATH_BANK_TO_LOG = new RSTile[] {
		new RSTile(2616, 3332, 0),
		new RSTile(2613, 3339, 0),
		new RSTile(2608, 3332, 0),
		new RSTile(2604, 3333, 0),
		new RSTile(2602, 3336, 0)
	};

	private static final RSTile[] PATH_LOG_TO_BANK = Walking.invertPath(PATH_BANK_TO_LOG);
	private static final RSTile[] PATH_LOG_TO_TOWER = Walking.invertPath(PATH_TOWER_TO_LOG);

	public static final RSTile POS_OBJ_LOG_BANK = new RSTile(2601, 3336, 0);
	public static final RSTile POS_OBJ_LOG_TOWER = new RSTile(2599, 3336, 0);

	private static GUI gui;

	// singleton
	public static GUI getGUI() {
		return gui = gui == null ? new GUI() : gui;
	}
	
	public static void refreshProtectedItems() {
		protectIDs = new ArrayList<Integer>(lootIDs);
		for (RSItem item : Equipment.getItems()) {
			protectIDs.add(item.getID());
		}
	}

	@Override
	public void run() {

		// wait until login bot is done.
		while (Login.getLoginState() != Login.STATE.INGAME)
			sleep(250);

		useLogCrossing = org.tribot.api2007.Skills.getActualLevel(SKILLS.AGILITY) >= 33;

		// We do this after making sure we logged in, otherwise we can't get the xp.
		Skills.setStartSkills(new SKILLS[] {SKILLS.ATTACK, SKILLS.STRENGTH, SKILLS.DEFENCE, SKILLS.HITPOINTS, SKILLS.RANGED, SKILLS.MAGIC });

		PaintHelper.showPaint = true;

		if (!useLogCrossing)
			General.println("[LAN ChaosKiller]: Detected that you are lower then 33 agility. We will walk over the bridge instead of the log.");

		// Tell the movement classes not to auto-find/open doors.
		// The only door we encounter is scripted due to pick-lock.
		Movement.setUseCustomDoors(true, new RSObject[] {});

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				getGUI().setVisible(true);
			}});

		while (waitForGUI)
			sleep(250);

		General.useAntiBanCompliance(true);

		org.tribot.api2007.Combat.setAutoRetaliate(true);

		new Thread(new StuckChecker()).start();

		while (!quitting) {
			State.getState().run();
			sleep(50);
		}
	}

	/**
	 * Handles the banking logic when at the bank.
	 */
	public static void doBanking() {

		statusText = "Banking";

		if (Banking.openBank()) {

			// Pin is handled by Tribot.

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Banking.isBankScreenOpen();
				}}, General.random(3000, 4000));


			Banking.depositAll();

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Inventory.getAll().length == 0;
				}}, General.random(3000, 4000));

			if (foodCount > 0) {

				Banking.withdraw(foodCount, foodName);

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return Inventory.getCount(new String[]{foodName}) >= foodCount;
					}}, General.random(3000, 4000));
			}

			Banking.close();
		}
	}

	/**
	 * Checks if there are items if we want on the ground, and if so, loot them.
	 * Drops items we accidently picked up.
	 */
	public static void doLooting() {
		if (lootIDs != null && lootIDs.size() > 0) {

			final int[] ids = buildIntArray(lootIDs);
			final RSGroundItem[] lootItems = GroundItems.find(ids);

			if (lootItems.length > 0) {
				Looting.lootGroundItems(lootItems, 0);
			}
		}
		
		// Drop anything except the items we want to loot or our equipment.
		Inventory.dropAllExcept(buildIntArray(protectIDs));
	}

	/**
	 * Handles the finding & killing logic of chaos druids when we are in the tower.
	 */
	public static void doKillDruids() {

		if (!Combat.isUnderAttack()) {

			doLooting();

			statusText = "Finding druids";

			final RSNPC npcs[] = Antiban.orderOfAttack(NPCs.findNearest("Chaos druid"));

			if (npcs == null || npcs.length == 0) {
				// Nothing to attack, idling
				Antiban.doIdleActions();
				wasIdle = true;
				idleSince = System.currentTimeMillis();
				return;
			}

			// NPCs to attack are found.

			// if we didn't have to turn the run on, we have to wait a small 'reaction time' before attacking the new npc.
			// The reason why i do this only if run wasn't set is because run takes time itself to set (longer then our delays), so it would wait double and be un-humanlike.
			if (!Antiban.doActivateRun()){

				if (wasIdle && idleSince > (System.currentTimeMillis() + General.random(8000, 12000))) {
					// If we were idle (waiting for spawn) for more then 8-12 sec
					// we should see this as a 'new' action and wait an appropriate amount of time.
					Antiban.doDelayForNewObject();
				} else {
					// if we were not idling (new npc was already spawned while fighting old, or within the 8-12sec after death)
					// we should see this as a 'switch' action and wait an appropriate amount of time.
					Antiban.doDelayForSwitchObject();
				}
			}

			statusText = "Killing druids";

			if (Combat.attackNPCs(npcs, true)) {

				if (!Combat.isUnderAttack()){
					doLooting();
					Antiban.doIdleActions();
				}
				wasIdle = false;
			}
		}
	}

	/**
	 * Handles the movement logic towards the bank.
	 */
	public static void goToBank() {

		if (AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition()))
			return;

		// We are in the tower, first open the door.
		if (Player.getPosition().distanceTo(POS_DRUID_TOWER_CENTER) <= 2) {

			statusText = "Opening door";

			Objects.interact("Open");

			// if it doesn't break early, we recurse call it again.
			if (!Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getPosition().distanceTo(POS_DRUID_TOWER_CENTER) >= 3;
				}}, General.random(2000, 3000)))
				return;
		}

		// if we are left from river and should use the log crossing
		if (useLogCrossing && Player.getPosition().getX() < COORD_X_RIVER) {

			statusText = "Going to log";

			Walking.walkPath(PATH_TOWER_TO_LOG);

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getPosition().distanceTo(PATH_TOWER_TO_LOG[PATH_TOWER_TO_LOG.length-1]) < 3;
				}}, General.random(18000, 20000));

			statusText = "Crossing log";

			for (int i = 0; i < 10; i++) {

				Objects.interact("Walk-across", POS_OBJ_LOG_TOWER);

				if (Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return Player.getPosition().getX() > COORD_X_RIVER;
					}}, General.random(2000, 3000)))
					break;
			}

		}


		statusText = "Going to bank";


		if (useLogCrossing) {
			if (!Walking.walkPath(PATH_LOG_TO_BANK)) {
				Movement.walkTo(POS_BANK_CENTER);
			}
		}
		else 
			Movement.walkTo(POS_BANK_CENTER);
	}

	/**
	 * Handles the movement logic towards the tower with druids.
	 */
	public static void goToDruids() {

		if (AREA_DOWNSTAIRS_TOWER.contains(Player.getPosition()))
			return;

		if (useLogCrossing && Player.getPosition().getX() > COORD_X_RIVER) {

			statusText = "Going to log";

			Walking.walkPath(PATH_BANK_TO_LOG);

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getPosition().distanceTo(PATH_BANK_TO_LOG[PATH_BANK_TO_LOG.length-1]) < 3;
				}}, General.random(18000, 20000));

			statusText = "Crossing log";

			for (int i = 0; i < 10; i++) {

				Objects.interact("Walk-across", POS_OBJ_LOG_BANK);

				if (Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(50);
						return Player.getPosition().getX() < COORD_X_RIVER;
					}}, General.random(2000, 3000))) 
					break;
			}

		}


		statusText = "Going to tower";

		if (useLogCrossing) 
			Walking.walkPath(PATH_LOG_TO_TOWER);
		else 
			Movement.walkTo(POS_OUTSIDE_DRUID_TOWER_DOOR);
	}

	/**
	 * Handles the logic to pick-lock the tower door.
	 */
	public static void doPicklockDoor() {

		statusText = "Picklocking door";

		// Always rotate camera when picklocking, else it might have a hard time clicking.
		Camera.turnToTile(POS_OUTSIDE_DRUID_TOWER_DOOR);

		if (Objects.interact("Pick-lock")) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(50);
					return Player.getPosition().distanceTo(POS_DRUID_TOWER_CENTER) < 3;
				}}, General.random(100, 200));
		}
	}

	/**
	 * Converts an Arraylist full of Integers into a int[]
	 * 
	 * @param integers
	 * @return
	 */
	public static int[] buildIntArray(ArrayList<Integer> integers) {
		int[] ints = new int[integers.size()];
		int i = 0;
		for (Integer n : integers) {
			ints[i++] = n;
		}
		return ints;
	}

	// Paint is handled in different file for better readability
	public void onPaint(Graphics g) { PaintHelper.onPaint(g); }

	// Mouse actions below are for hiding/showing paint.
	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_CLICKED) {

			if (PaintHelper.paintToggle.contains(e.getPoint())) {

				PaintHelper.showPaint = !PaintHelper.showPaint;

				e.consume();
				return OVERRIDE_RETURN.DISMISS;
			} else if (PaintHelper.settingsToggle.contains(e.getPoint())) {

				LANChaosKiller.getGUI().setVisible(!LANChaosKiller.getGUI().isVisible());

				e.consume();
				return OVERRIDE_RETURN.DISMISS;
			}
		}

		return OVERRIDE_RETURN.PROCESS;
	}


	public void paintMouse(Graphics g, Point mousePos, Point dragPos) {
		Paint.drawMouse(g, mousePos, dragPos);
	}

	public void mouseClicked(Point p, int button, boolean isBot) {
		Paint.mouseDown = true;
	}

	public void paintMouseSpline(Graphics g, ArrayList<Point> points) {} // remove mouse trail

	// unused overrides
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent e) {return OVERRIDE_RETURN.SEND;}
	public boolean randomFailed(RANDOM_SOLVERS random) { isDoingRandom = false; return true; }
	public void randomSolved(RANDOM_SOLVERS random) {isDoingRandom = false;}
	public void mouseReleased(Point p, int button, boolean isBot) {}
	public void mouseDragged(Point p, int movePos, boolean dragPos) {}
	public void mouseMoved(Point p, boolean isBot) {}

}