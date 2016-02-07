package scripts.LANChaosKiller.UI;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import scripts.LanAPI.Core.Types.StringUtils;
import scripts.LanAPI.Game.Antiban.Antiban;
import scripts.LanAPI.Game.Combat.Combat;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Painting.PaintString;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Laniax
 */
public class PaintInfo extends AbstractPaintInfo {

    final BufferedImage _bg, _toggle, _bar;
    final Font lightSmall, lightMed, lightLarge;
    final Font mediumSmall, mediumMed, mediumLarge;

    final Point bgPos = new Point(2, 233);
    final Point barPos = new Point(7, 319);
    final Point runtimePos = new Point(383, 262);
    final Point profitPos = new Point(370, 288);
    final Point profitPHPos = new Point(370, 305);
    final Point druidsPos = new Point(290, 288);
    final Point druidsPHPos = new Point(290, 305);
    final Point statusPos = new Point(166, 262);

    final Rectangle centerProfit = new Rectangle(profitPos.x, profitPos.y, 70, 10);
    final Rectangle centerProfitPH = new Rectangle(profitPHPos.x, profitPHPos.y, 70, 10);

    final Rectangle centerDruids = new Rectangle(druidsPos.x, druidsPos.y, 70, 10);
    final Rectangle centerDruidsPH = new Rectangle(druidsPHPos.x, druidsPHPos.y, 70, 10);

    private PaintString ttlString = null;

    final BufferedImage attackIcon, strengthIcon, defenceIcon, hitpointsIcon, rangedIcon, magicIcon;

    private final int[][] skillXPLocations = new int[][]{
            {85, 283},
            {85, 298},
            {85, 313},

            {200, 283},
            {200, 298},
            {200, 313},
    };

    public PaintInfo() {

        _bg = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/bg.png");
        _toggle = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptPaintToggle.png");
        _bar = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/bar.png");
        lightMed = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/Roboto-Light.ttf", 12.5f);
        mediumMed = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/Roboto-Medium.ttf", 12.5f);

        setBackgroundPosition(bgPos);

        attackIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/attack.png");
        strengthIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/strength.png");
        defenceIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/defence.png");
        hitpointsIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/hitpoints.png");
        rangedIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/ranged.png");
        magicIcon = PaintHelper.getBufferedImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Paint/mage.png");

        if (lightMed != null) {
            lightSmall = lightMed.deriveFont(10.5f);
            lightLarge = lightMed.deriveFont(33f);
        } else {
            lightSmall = null;
            lightLarge = null;
        }

        if (mediumMed != null) {
            mediumSmall = mediumMed.deriveFont(12.5f);
            mediumLarge = mediumMed.deriveFont(20.5f);
        } else {
            mediumSmall = null;
            mediumLarge = null;
        }
    }

    @Override
    public Image getBackground() {
        return _bg;
    }

    @Override
    public Image getButtonPaintToggle() {
        return _toggle;
    }

    @Override
    public List<PaintString> getText(long runTime, Graphics2D g) {

        double hours = runTime / 3600000.0;

        List<PaintString> result = new ArrayList<>();

        result.add(new PaintString(PaintHelper.statusText, statusPos, lightMed, Color.WHITE, true));

        result.add(new PaintString(Timing.msToString(runTime), runtimePos, lightMed, Color.WHITE, true));

        PaintString ps = new PaintString(PaintHelper.formatNumber(PaintHelper.profit), profitPos, mediumLarge, Color.WHITE, true);
        ps.setCentered(centerProfit);
        result.add(ps);

        ps = new PaintString(String.format("(%s / hour)", PaintHelper.formatNumber((int) Math.round(PaintHelper.profit / hours))), profitPHPos, lightMed, Color.WHITE, true);
        ps.setCentered(centerProfitPH);
        result.add(ps);

        ps = new PaintString(PaintHelper.formatNumber(Antiban.getResourcesWon()), druidsPos, mediumLarge, Color.WHITE, true);
        ps.setCentered(centerDruids);
        result.add(ps);

        ps = new PaintString(String.format("(%s / hour)", PaintHelper.formatNumber((int) Math.round(Antiban.getResourcesWon() / hours))), druidsPHPos, lightMed, Color.WHITE, true);
        ps.setCentered(centerDruidsPH);
        result.add(ps);

        Skills.SKILLS skill = SkillsHelper.getSkillWithMostIncrease();
        if (skill != null && _bar != null) {
            long xpGained = SkillsHelper.getReceivedXP(skill);
            long xpToNextLevel = Skills.getXPToNextLevel(skill);
            int xpPerHour = (int)(xpGained / hours);
            long ttlNumber = ((xpToNextLevel * 3600000L) / xpPerHour);

            String ttl = Timing.msToString(ttlNumber);

            Point pos = new Point(_bar.getWidth() / 2 + barPos.x, barPos.y);

            ps = new PaintString(String.format(" %d%% to level %d (Time to next level: %s)", Skills.getPercentToNextLevel(skill), Skills.getActualLevel(skill) + 1, ttl), pos, mediumSmall, Color.WHITE, false);

            BufferedImage icon = getIconForSkill(skill);

            ps.setCentered(new Rectangle(barPos.x, barPos.y, _bar.getWidth(), _bar.getHeight()));
            ps.setIcon(icon);

            ttlString = ps;

            result.add(ps);
        }

        int i = 0;
        for (Map.Entry<Skills.SKILLS, Integer> s : SkillsHelper.getStartSkills().entrySet()) {

            int xpGained = SkillsHelper.getReceivedXP(s.getKey());
            if (xpGained == 0)
                continue;

            String xpHour = PaintHelper.formatNumber((int)Math.round(xpGained / hours));

            String str = String.format("%s (%s / hour)", PaintHelper.formatNumber(xpGained), xpHour);
            Point pos = new Point(skillXPLocations[i][0], skillXPLocations[i][1]);

            PaintString paintString = new PaintString(str, pos, lightSmall, Color.WHITE, true);

            BufferedImage icon = getIconForSkill(s.getKey());
            paintString.setIcon(icon);

            result.add(paintString);

            i++;
        }

        return result;
    }

    @Override
    public void customDraw(Graphics2D g) {

        if (_bar == null)
            return;

        Shape oldClip = g.getClip();

        Skills.SKILLS skill = SkillsHelper.getSkillWithMostIncrease();

        if (skill != null) {

            int percentage = Skills.getPercentToNextLevel(skill);

            int barWidth = (_bar.getWidth() / 100) * percentage;
            Rectangle clip = new Rectangle(barPos.x, barPos.y, barWidth, _bar.getHeight());

            g.setClip(clip);

            g.drawImage(_bar, barPos.x, barPos.y, null);

            if (ttlString != null) {
                ttlString.setColor(Color.BLACK);
                PaintHelper.drawPaintString(ttlString, g);
            }

            g.setClip(oldClip);

        }
    }

    private BufferedImage getIconForSkill(Skills.SKILLS skill) {

        switch (skill) {
            case ATTACK: return attackIcon;
            case STRENGTH: return strengthIcon;
            case DEFENCE: return defenceIcon;
            case HITPOINTS: return hitpointsIcon;
            case RANGED: return rangedIcon;
            case MAGIC: return magicIcon;
        }
        return null;
    }
}
