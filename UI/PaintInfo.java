package scripts.LANChaosKiller.UI;

import org.tribot.api2007.Skills;
import scripts.lanapi.core.types.StringUtils;
import scripts.lanapi.game.antiban.Antiban;
import scripts.lanapi.game.helpers.SkillsHelper;
import scripts.lanapi.game.painting.AbstractPaintInfo;
import scripts.lanapi.game.painting.PaintBuilder;
import scripts.lanapi.game.painting.PaintHelper;

import java.awt.*;

/**
 * @author Laniax
 */
public class PaintInfo extends AbstractPaintInfo {

    /**
     * Return the primary color of your script. This will determine cursor color etc.
     *
     * @return
     */
    @Override
    public Color getPrimaryColor() {
        return new Color(255, 158, 0);
    }

    /**
     * Return the secondary color of your script. This will determine values inside texts etc.
     *
     * @return
     */
    @Override
    public Color getSecondaryColor() {
        return getPrimaryColor();
    }

    /**
     * Return if the script is a premium script or not, determines the FREE or PREMIUM tag in the paint.
     *
     * @return
     */
    @Override
    public boolean isScriptPremium() {
        return false;
    }

    /**
     * Returns if the bugreport button should be shown.
     *
     * @return
     */
    @Override
    public boolean showReportBugButton() {
        return false;
    }

    /**
     * Returns how the title should look in the paint.
     * @return
     */
    @Override
    public PaintBuilder paintTitle() {
        return new PaintBuilder()
                .add()
                .setColor(Color.white)
                .setText("LAN ")
                .setColor(this.primary)
                .setText("Chaos Killer")
                .end();
    }

    /**
     * Return all the custom lines you want to display in the paint.
     * Most likely the XP, profit, etc.
     * @param runTime
     * @return
     */
    @Override
    public PaintBuilder getText(long runTime) {

        double hours = runTime / 3600000.0;

        Skills.SKILLS skill = SkillsHelper.getSkillWithMostIncrease();

        if (skill == null)
            skill = Skills.SKILLS.STRENGTH;

        int xpGained = SkillsHelper.getReceivedXP(skill);

        return new PaintBuilder()
                .add()
                    .setColor(Color.white)
                    .setText("Profit ")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber(PaintHelper.profit, true))
                    .setColor(Color.white)
                    .setText(" (")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber((int) Math.round(PaintHelper.profit / hours), true))
                    .setColor(Color.white)
                    .setText("/h)")
                .end()
                .add()
                    .setColor(Color.white)
                    .setText("Killcount ")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber(Antiban.getResourcesWon()))
                    .setColor(Color.white)
                    .setText(" (")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber((int) Math.round(Antiban.getResourcesWon() / hours)))
                    .setColor(Color.white)
                    .setText("/h)")
                .end()
                .add()
                    .setColor(Color.white)
                    .setText(StringUtils.capitalize(skill.name()) + " ")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber(xpGained))
                    .setColor(Color.white)
                    .setText(" (")
                    .setColor(this.secondary)
                    .setText(PaintHelper.formatNumber((int) (xpGained / hours)))
                    .setColor(Color.white)
                    .setText("/h)")
                .end();
    }
}
