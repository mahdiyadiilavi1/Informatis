package UnitInfo;

import UnitInfo.core.*;
import UnitInfo.shaders.LineShader;
import UnitInfo.shaders.RangeShader;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Teamc;

import static arc.Core.atlas;
import static arc.Core.settings;

public class SVars {
    public static HudUi hud = new HudUi();
    public static TextureRegion clear = atlas.find("clear");
    public static TextureRegion error = atlas.find("error");
    public static RangeShader turretRange;
    public static LineShader lineShader;
    public static Teamc target;
    public static boolean locked;
    public static boolean jsonGen = false;
    public static float uiResumeRate = 3 * 60f; //default 3s
}
