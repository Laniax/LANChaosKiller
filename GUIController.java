package scripts.LANChaosKiller;

import com.allatori.annotations.DoNotRename;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import scripts.LANChaosKiller.Constants.ItemIDs;
import scripts.LanAPI.Core.GUI.AbstractGUIController;
import scripts.LanAPI.Core.GUI.GUI;
import scripts.LanAPI.Core.System.NotificationPreferences;
import scripts.LanAPI.Core.System.Notifications;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Persistance.Variables;
import scripts.LanAPI.Network.Internet;
import scripts.LanAPI.Network.ItemPrice;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * @author Laniax
 */
@DoNotRename
public class GUIController extends AbstractGUIController {

    private Preferences preferences = null;

    private HashMap<ItemIDs, CheckBox> items;

    @FXML
    public CheckBox equipBolts;

    @FXML
    public CheckBox worldhop;

    @FXML
    public TextField foodName;

    @FXML
    public Label threadLink;

    @FXML
    public CheckBox lootGuam;

    @FXML
    public CheckBox lootTarromin;

    @FXML
    public CheckBox lootIrit;

    @FXML
    public CheckBox lootMarrentill;

    @FXML
    public CheckBox lootHarralander;

    @FXML
    public CheckBox lootAvantoe;

    @FXML
    public CheckBox lootRanarr;

    @FXML
    public CheckBox lootKwuarm;

    @FXML
    public CheckBox lootCadantine;

    @FXML
    public CheckBox lootLantadyme;

    @FXML
    public CheckBox lootDwarfweed;

    @FXML
    public CheckBox lootLaw;

    @FXML
    public CheckBox lootNature;

    @FXML
    public CheckBox lootBolts;

    @FXML
    public CheckBox lootEnsouled;

    @FXML
    public CheckBox lootAbove;

    @FXML
    public Spinner<Integer> lootAboveAmount;

    @FXML
    public Spinner<Integer> foodCount;

    @FXML
    public GridPane notGroup;

    @FXML
    public CheckBox notChat;

    @FXML
    public CheckBox notPM;

    @FXML
    public CheckBox notBreakStart;

    @FXML
    public CheckBox notTrade;

    @FXML
    public CheckBox notServer;

    @FXML
    public CheckBox notClan;

    @FXML
    public CheckBox notSkill;

    @FXML
    public CheckBox notBreakEnd;

    @FXML
    public CheckBox notWorldhop;

    @FXML
    public CheckBox notBanking;

    @FXML
    public CheckBox enableNotifications;

    @FXML
    public Button startScript;

    @FXML
    public TextField scriptArguments;

    private void initMap() {

        items = new HashMap<>();

        items.put(ItemIDs.GUAM_LEAF, lootGuam);
        items.put(ItemIDs.MARRENTILL, lootMarrentill);
        items.put(ItemIDs.TARROMIN, lootTarromin);
        items.put(ItemIDs.HARRALANDER, lootHarralander);
        items.put(ItemIDs.RANARR, lootRanarr);
        items.put(ItemIDs.IRIT, lootIrit);
        items.put(ItemIDs.AVANTOE, lootAvantoe);
        items.put(ItemIDs.KWUARM, lootKwuarm);
        items.put(ItemIDs.CADANTINE, lootCadantine);
        items.put(ItemIDs.DWARF_WEED, lootDwarfweed);
        items.put(ItemIDs.LANTADYME, lootLantadyme);
        items.put(ItemIDs.LAW_RUNE, lootLaw);
        items.put(ItemIDs.NATURE_RUNE, lootNature);
        items.put(ItemIDs.MITHRIL_BOLTS, lootBolts);
        items.put(ItemIDs.ENSOULED_HEAD, lootEnsouled);

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initMap();

        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 27);
        foodCount.setValueFactory(factory);

        SpinnerValueFactory<Integer> factory2 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE);
        lootAboveAmount.setValueFactory(factory2);

        load();

        foodCount.valueProperty().addListener((obs, oldVal, newVal) -> { refreshArguments(); });
        lootAboveAmount.valueProperty().addListener((obs, oldVal, newVal) -> {
//            log.info("getShouldLootAboveAmount is returning "+ lootAboveAmount.valueProperty().getValue());
            refreshArguments();
        });

        foodName.setOnAction(event -> refreshArguments());
        worldhop.setOnAction((event) -> refreshArguments());
        equipBolts.setOnAction((event) -> refreshArguments());

        for (Map.Entry<ItemIDs, CheckBox> set : items.entrySet()) {

            CheckBox checkBox = set.getValue();

            checkBox.setOnAction((event) -> refreshArguments());

//            String formattedPrice = PaintHelper.formatNumber(ItemPrice.get(set.getKey().getID()));
//            Tooltip tooltip = new Tooltip(String.format("Value: %s.", formattedPrice));

            // price tooltip
            Tooltip tooltip = new Tooltip(String.format("Value: %sgp.", ItemPrice.get(set.getKey().getID())));
            checkBox.setTooltip(tooltip);
        }

        // Notifications set enable / disable
        enableNotifications.setOnAction((event) -> {
            notGroup.setDisable(!enableNotifications.isSelected());
            refreshArguments();
        });

        // Thread link
        threadLink.setOnMouseClicked(event -> Internet.openWebsite("https://tribot.org/forums/topic/22590-"));

        // Start button
        startScript.setOnAction((event) -> {
            save();

            GUI gui = getGUI();

            if (gui != null)
                gui.close();
        });
    }

    @Override
    public boolean getEnableNotifications() {
        return enableNotifications.isSelected();
    }

    private void load() {

        // Load from persistance
        try {
            preferences = Preferences.userRoot().node("LanChaosKiller_UserSettings");

            foodName.setText(preferences.get("foodName", "Lobster"));

            SpinnerValueFactory<Integer> factory = foodCount.getValueFactory();
            factory.setValue(preferences.getInt("foodCount", 1));

            worldhop.setSelected(preferences.getBoolean("worldhop", true));
            equipBolts.setSelected(preferences.getBoolean("equipBolts", true));
            lootAbove.setSelected(preferences.getBoolean("lootAbove", true));

            SpinnerValueFactory<Integer> factory2 = lootAboveAmount.getValueFactory();
            factory2.setValue(preferences.getInt("lootAboveAmount", 1));

            // Notifications
            enableNotifications.setSelected(preferences.getBoolean("enableNotifications", true));
            notGroup.setDisable(!enableNotifications.isSelected());
            notChat.setSelected(preferences.getBoolean("notChat", true));
            notPM.setSelected(preferences.getBoolean("notPM", true));
            notBreakStart.setSelected(preferences.getBoolean("notBreakStart", true));
            notTrade.setSelected(preferences.getBoolean("notTrade", true));
            notServer.setSelected(preferences.getBoolean("notServer", true));
            notClan.setSelected(preferences.getBoolean("notClan", true));
            notSkill.setSelected(preferences.getBoolean("notSkill", true));
            notBreakEnd.setSelected(preferences.getBoolean("notBreakEnd", true));
            notWorldhop.setSelected(preferences.getBoolean("notWorldhop", true));
            notBanking.setSelected(preferences.getBoolean("notBanking", true));

            //Items
            for (Map.Entry<ItemIDs, CheckBox> set : items.entrySet()) {
                set.getValue().setSelected(preferences.getBoolean(set.getKey().name(), false));
            }

            refreshArguments();

//            log.debug("Loaded settings from preferences");

        } catch (Exception e) {
//            log.error("Error while loading settings from last time. This is caused by some VPS's. Specific message: '%s'.", e.getMessage());
        }

    }

    private void save() {

        Combat.setFoodName(getFoodName());

        Variables.getInstance().addOrUpdate("foodCount", getFoodCount());
        Variables.getInstance().addOrUpdate("equipBolts", getEquipBolts());
        Variables.getInstance().addOrUpdate("worldhop", getShouldWorldhop());
        Variables.getInstance().addOrUpdate("lootAbove", getShouldLootAbove());
        Variables.getInstance().addOrUpdate("lootAboveAmount", getShouldLootAboveAmount());

        Variables.getInstance().addOrUpdate("enableNotifications", getEnableNotifications());

        NotificationPreferences prefs = Notifications.getPreferences();
        prefs.setOnChatMessage(notChat.isSelected());
        prefs.setOnPrivateMessage(notPM.isSelected());
        prefs.setOnBreakStart(notBreakStart.isSelected());
        prefs.setOnBreakEnd(notBreakEnd.isSelected());
        prefs.setOnTradeRequest(notTrade.isSelected());
        prefs.setOnServerMessage(notServer.isSelected());
        prefs.setOnClanMessage(notClan.isSelected());
        prefs.setOnSkillLevelUp(notSkill.isSelected());

        Variables.getInstance().addOrUpdate("worldhopNotification", notWorldhop.isSelected());
        Variables.getInstance().addOrUpdate("bankingNotification", notBanking.isSelected());

        for (Map.Entry<ItemIDs, CheckBox> set : items.entrySet()) {
            set.getKey().shouldLoot(set.getValue().isSelected());
        }

        // Persist settings
        try {
            preferences = Preferences.userRoot().node("LanChaosKiller_UserSettings");

            preferences.put("foodName", getFoodName());
            preferences.putInt("foodCount", getFoodCount());
            preferences.putBoolean("equipBolts", getEquipBolts());

            preferences.putBoolean("worldhop", getShouldWorldhop());
            preferences.putBoolean("lootAbove", getShouldLootAbove());
            preferences.putInt("lootAboveAmount", getShouldLootAboveAmount());

            //notifications
            preferences.putBoolean("enableNotifications", getEnableNotifications());
            preferences.putBoolean("notChat", notChat.isSelected());
            preferences.putBoolean("notPM", notPM.isSelected());
            preferences.putBoolean("notBreakStart", notBreakStart.isSelected());
            preferences.putBoolean("notTrade", notTrade.isSelected());
            preferences.putBoolean("notServer", notServer.isSelected());
            preferences.putBoolean("notClan", notClan.isSelected());
            preferences.putBoolean("notSkill", notSkill.isSelected());
            preferences.putBoolean("notBreakEnd", notBreakEnd.isSelected());
            preferences.putBoolean("notWorldhop", notWorldhop.isSelected());
            preferences.putBoolean("notBanking", notBanking.isSelected());

            // All items
            for (Map.Entry<ItemIDs, CheckBox> set : items.entrySet()) {
                preferences.putBoolean(set.getKey().name(), set.getValue().isSelected());
            }

//            log.debug("Saved settings to preferences");


        } catch (Exception e) {
//            log.error("Error while saving these settings for next time. This is caused by some VPS's.");
        }
    }

    private void refreshArguments() {

        final StringBuilder sb = new StringBuilder();

        String foodname = getFoodName();
        if (!foodname.isEmpty())
            sb.append(String.format("foodname: %s;", foodname));

        sb.append(String.format("foodcount: %d;", getFoodCount()));

        String excludeList = "";
        for (Map.Entry<ItemIDs, CheckBox> set : items.entrySet()) {
            if (!set.getValue().isSelected())
                excludeList += String.format(" %s,", set.getKey().name());
        }

        if (!excludeList.isEmpty()) {
            excludeList = excludeList.substring(0,excludeList.length()-1); // remove last ,
            sb.append(String.format("exclude:%s;", excludeList));
        }

        sb.append(String.format("equipbolts: %b;", getEquipBolts()));
        sb.append(String.format("worldhop: %b;", getShouldWorldhop()));

        if (getShouldLootAbove())
            sb.append(String.format("lootabove: %d;", getShouldLootAboveAmount()));

        sb.append(String.format("notifications: %b;", enableNotifications.isSelected()));

        scriptArguments.setText(sb.toString());

    }

    public String getFoodName() {
        return foodName.getText();
    }

    public int getFoodCount() {
        return foodCount.getValue();
    }

    public boolean getEquipBolts() {
        return equipBolts.isSelected();
    }

    public boolean getShouldWorldhop() {
        return worldhop.isSelected();
    }

    public boolean getShouldLootAbove() {
        return lootAbove.isSelected();
    }

    public int getShouldLootAboveAmount() {
//        log.info("getShouldLootAboveAmount is returning "+ lootAboveAmount.getValue());
        return lootAboveAmount.getValue();
    }
}
