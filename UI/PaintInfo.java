package scripts.LANChaosKiller.UI;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import scripts.LanAPI.Game.Helpers.SkillsHelper;
import scripts.LanAPI.Game.Painting.AbstractPaintInfo;
import scripts.LanAPI.Game.Painting.PaintHelper;
import scripts.LanAPI.Game.Painting.PaintString;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Laniax
 */
public class PaintInfo extends AbstractPaintInfo {

    final Image _bg = PaintHelper.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptPaint.png");
    final Image _toggle = PaintHelper.getImage("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/scriptPaintToggle.png");

    final Font fontMed = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/ChaosKiller/Script/SF%20Electrotome.ttf", 22f);
    final Font fontSmall = fontMed.deriveFont(18f);
    final Font fontLarge = fontMed.deriveFont(33f);

    final Point runtimePos = new Point(115, 406);
    final Point statusPos = new Point(141, 372);

    private final int[][] skillXPLocations = new int[][]{
            {60, 433},
            {60, 458},
            {60, 483},

            {240, 433},
            {240, 458},
            {240, 483},
    };

    @Override
    public Image getBackground() {
        return _bg;
    }

    @Override
    public Image getButtonPaintToggle() {
        return _toggle;
    }

    @Override
    public List<PaintString> getText(long runTime) {

        List<PaintString> result = new ArrayList<>();

        result.add(new PaintString(PaintHelper.statusText, statusPos, fontLarge, Color.WHITE, true));

        result.add(new PaintString(Timing.msToString(runTime), runtimePos, fontMed, Color.WHITE, true));

        int i = 0;
        for (Map.Entry<Skills.SKILLS, Integer> s : SkillsHelper.getStartSkills().entrySet()) {

            int xpGained = SkillsHelper.getReceivedXP(s.getKey());

            double hours = runTime / 3600000.0;

            String xpHour = NumberFormat.getNumberInstance().format(Math.round(xpGained / hours));

            String str = String.format("%d (%s / hour)", xpGained, xpHour);
            Point pos = new Point(skillXPLocations[i][0], skillXPLocations[i][1]);

            result.add(new PaintString(str, pos, fontSmall, Color.WHITE, true));
            i++;
        }

        return result;
    }
}
