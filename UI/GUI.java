package scripts.LANChaosKiller.UI;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LanAPI.Game.Persistance.Variables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

public class GUI extends JFrame {

    private static Preferences preferences = null;
    private HashMap<JCheckBox, ItemIDs> checkBoxes = new HashMap<>();
    Point start_drag, start_loc;

    public GUI() {

        boolean lootRareDrops = false;
        List<Integer> lootList = new ArrayList<>();

        // Load GUI settings
        try {
            preferences = Preferences.userRoot().node("LanChaosKiller_UserSettings");
            Mouse.setSpeed(preferences.getInt("mouseSpeed", 70));
            Variables.getInstance().addOrUpdate("foodCount", preferences.getInt("foodCount", 1));
            Variables.getInstance().addOrUpdate("foodName", preferences.get("foodName", "Lobster"));

            for (ItemIDs i : ItemIDs.values()) {
                if (preferences.getBoolean(i.name(), false)) {
                    if (i == ItemIDs.ALL_RARES) {
                        lootList.add(ItemIDs.SHIELD_LEFT_HALF.getID());
                        lootList.add(ItemIDs.HALF_KEY_TOOTH.getID());
                        lootList.add(ItemIDs.HALF_KEY_LOOP.getID());
                        lootList.add(ItemIDs.DRAGONSTONE.getID());
                        lootList.add(ItemIDs.DRAGON_SPEAR.getID());

                        lootRareDrops = true;
                    } else {
                        lootList.add(i.getID());
                    }
                }
            }
        } catch (Exception e) {
            General.println("Error while loading settings from last time. This is caused by some VPS's.");
        }

        JCheckBox lootGuam = new JCheckBox();
        JCheckBox lootMarrentill = new JCheckBox();
        JCheckBox lootTarromin = new JCheckBox();
        JCheckBox lootHarralander = new JCheckBox();
        JCheckBox lootRanarr = new JCheckBox();
        JCheckBox lootIrit = new JCheckBox();
        JCheckBox lootAventoe = new JCheckBox();
        JCheckBox lootKwuarm = new JCheckBox();
        JCheckBox lootCadantine = new JCheckBox();
        JCheckBox lootDwarf = new JCheckBox();
        JCheckBox lootLantadyme = new JCheckBox();
        JCheckBox lootLaw = new JCheckBox();
        JCheckBox lootNature = new JCheckBox();
        JCheckBox lootBolts = new JCheckBox();
        JCheckBox lootJavelin = new JCheckBox();

        checkBoxes.put(lootGuam, ItemIDs.GUAM_LEAF);
        checkBoxes.put(lootMarrentill, ItemIDs.MARRENTILL);
        checkBoxes.put(lootTarromin, ItemIDs.TARROMIN);
        checkBoxes.put(lootHarralander, ItemIDs.HARRALANDER);
        checkBoxes.put(lootRanarr, ItemIDs.RANARR);
        checkBoxes.put(lootIrit, ItemIDs.IRIT);
        checkBoxes.put(lootAventoe, ItemIDs.AVANTOE);
        checkBoxes.put(lootKwuarm, ItemIDs.KWUARM);
        checkBoxes.put(lootCadantine, ItemIDs.CADANTINE);
        checkBoxes.put(lootDwarf, ItemIDs.DWARF_WEED);
        checkBoxes.put(lootLantadyme, ItemIDs.LANTADYME);
        checkBoxes.put(lootLaw, ItemIDs.LAW_RUNE);
        checkBoxes.put(lootNature, ItemIDs.NATURE_RUNE);
        checkBoxes.put(lootBolts, ItemIDs.MITHRIL_BOLTS);
        checkBoxes.put(lootJavelin, ItemIDs.RUNE_JAVELIN);
        checkBoxes.put(lootRares, ItemIDs.ALL_RARES);

        setTitle("LAN ChaosKiller - Settings");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setBounds(new Rectangle(0, 0, 344, 510));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        setPreferredSize(new Dimension(345, 510));
        getContentPane().setLayout(null);

        for (Entry<JCheckBox, ItemIDs> entry : checkBoxes.entrySet()) {
            JCheckBox checkBox = entry.getKey();
            checkBox.setOpaque(false);
            getContentPane().add(checkBox);
        }

        lootKwuarm.setBounds(250, 100, 100, 27);
        lootGuam.setBounds(20, 70, 100, 27);
        lootMarrentill.setBounds(20, 100, 100, 27);
        lootTarromin.setBounds(130, 40, 100, 27);
        lootHarralander.setBounds(130, 70, 110, 27);
        lootRanarr.setBounds(130, 100, 70, 27);
        lootIrit.setBounds(250, 40, 100, 27);
        lootAventoe.setBounds(250, 70, 100, 27);
        lootDwarf.setBounds(20, 170, 100, 27);
        lootCadantine.setBounds(20, 140, 100, 27);
        lootLantadyme.setBounds(130, 140, 110, 27);
        lootLaw.setBounds(20, 230, 70, 27);
        lootNature.setBounds(20, 260, 110, 27);
        lootBolts.setBounds(130, 230, 110, 27);
        lootJavelin.setBounds(130, 260, 110, 27);
        lootRares.setBounds(250, 230, 110, 27);

        mouseSpeed.setOpaque(false);
        getContentPane().add(mouseSpeed);
        mouseSpeed.setBounds(70, 345, 200, 23);
        mouseSpeed.setMaximum(175);
        mouseSpeed.setMinimum(10);
        mouseSpeed.setValue(Mouse.getSpeed());

        foodCountSpinner.setOpaque(false);
        foodCountSpinner.setModel(new SpinnerNumberModel(Variables.getInstance().<Integer>get("foodCount", 0).intValue(), 0, 28, 1));
        getContentPane().add(foodCountSpinner);
        foodCountSpinner.setBounds(280, 302, 40, 20);

        foodNameTextField.setOpaque(false);
        foodNameTextField.setText(Variables.getInstance().get("foodName", "Lobster"));
        getContentPane().add(foodNameTextField);
        foodNameTextField.setBounds(100, 302, 60, 20);

        JButton btnSave = new JButton();
        btnSave.setText("Save Settings");
        btnSave.setOpaque(false);
        btnSave.addActionListener(evt -> btnSaveSettingsClicked(evt));
        getContentPane().add(btnSave);
        btnSave.setBounds(90, 415, 170, 40);

        JLabel backgroundLabel = new JLabel();
        try {
            backgroundLabel.setIcon(new ImageIcon(new URL("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptSettings.png")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        backgroundLabel.setText("Failed to load background :(");
        backgroundLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                backgroundMousePressed(evt);
            }
        });
        backgroundLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                backgroundMouseDragged(evt);
            }
        });
        getContentPane().add(backgroundLabel);
        backgroundLabel.setBounds(0, 0, 337, 495);

        for (Integer id : lootList) {
            for (Entry<JCheckBox, ItemIDs> entry : checkBoxes.entrySet()) {
                if (id == entry.getValue().getID()) {
                    entry.getKey().setSelected(true);
                    break;
                }
            }
        }

        lootRares.setSelected(lootRareDrops);

        Variables.getInstance().addOrUpdate("lootList", lootList);

        this.setLocationRelativeTo(null);
        this.toFront();
    }

    protected void backgroundMousePressed(MouseEvent evt) {
        this.start_drag = this.getScreenLocation(evt);
        this.start_loc = this.getLocation();
    }

    Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()), (int) (target_location.getY() + cursor.getY()));
    }

    protected void backgroundMouseDragged(MouseEvent evt) {
        Point current = this.getScreenLocation(evt);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(), (int) current.getY() - (int) start_drag.getY());
        Point new_location = new Point((int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc.getY() + offset.getY()));
        this.setLocation(new_location);
    }

    protected void btnSaveSettingsClicked(ActionEvent evt) {

        List<Integer> lootList = new ArrayList<>();

        for (Entry<JCheckBox, ItemIDs> entry : checkBoxes.entrySet()) {
            if (entry.getKey().isSelected()) {
                if (entry == lootRares) {
                    lootList.add(ItemIDs.SHIELD_LEFT_HALF.getID());
                    lootList.add(ItemIDs.HALF_KEY_TOOTH.getID());
                    lootList.add(ItemIDs.HALF_KEY_LOOP.getID());
                    lootList.add(ItemIDs.DRAGONSTONE.getID());
                    lootList.add(ItemIDs.DRAGON_SPEAR.getID());
                } else {
                    lootList.add(entry.getValue().getID());
                }
            }
        }

        Variables.getInstance().addOrUpdate("lootList", lootList);
        Variables.getInstance().addOrUpdate("foodName", foodNameTextField.getText());
        Variables.getInstance().addOrUpdate("foodCount", foodCountSpinner.getValue());

        this.setVisible(false);

        // Save these settings.
        try {
            preferences = Preferences.userRoot().node("LanChaosKiller_UserSettings");
            preferences.putInt("mouseSpeed", mouseSpeed.getValue());
            preferences.put("foodName", foodNameTextField.getText());
            preferences.putInt("foodCount", (int) foodCountSpinner.getValue());

            boolean lootRares = false;

            for (ItemIDs i : ItemIDs.values()) {

                if (i == ItemIDs.ALL_RARES || i == ItemIDs.SHIELD_LEFT_HALF || i == ItemIDs.HALF_KEY_TOOTH || i == ItemIDs.HALF_KEY_LOOP || i == ItemIDs.DRAGONSTONE || i == ItemIDs.DRAGON_SPEAR) {

                    if (lootList.contains(i.getID()))
                        lootRares = true;
                    continue;
                }

                preferences.putBoolean(i.name(), lootList.contains(i.getID()));
            }

            preferences.putBoolean(ItemIDs.ALL_RARES.name(), lootRares);

        } catch (Exception e) {
            General.println("Error while saving these settings for next time. This is caused by some VPS's.");
        }
    }

    private JCheckBox lootRares = new JCheckBox();
    private JSlider mouseSpeed = new JSlider();
    private JSpinner foodCountSpinner = new JSpinner();
    private JTextField foodNameTextField = new JTextField();
}
