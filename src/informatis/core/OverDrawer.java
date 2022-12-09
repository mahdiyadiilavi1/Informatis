package informatis.core;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import informatis.ui.windows.UnitWindow;
import informatis.ui.managers.WindowManager;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static informatis.SUtils.getTarget;
import static arc.Core.*;
import static mindustry.Vars.*;

public class OverDrawer {

    public static void init(){
        Events.run(EventType.Trigger.draw, () -> {
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.z(Layer.max);
            if(settings.getBool("spawnerarrow")) {
                float leng = (player.unit() != null && player.unit().hitSize > 4 * 8f ? player.unit().hitSize * 1.5f : 4 * 8f) +  sin;
                Tmp.v1.set(camera.position);
                Lines.stroke(1f + sin / 2, Pal.accent);
                Lines.circle(Tmp.v1.x, Tmp.v1.y, leng - 4f);
                spawner.getSpawns().each(t -> Drawf.arrow(Tmp.v1.x, Tmp.v1.y, t.worldx(), t.worldy(), leng, (Math.min(200 * 8f, Mathf.dst(Tmp.v1.x, Tmp.v1.y, t.worldx(), t.worldy())) / (200 * 8f)) * (5f +  sin)));
            }

            if(settings.getBool("select")) {
                WindowManager.windows.get(UnitWindow.class).each(w -> {
                    UnitWindow window = (UnitWindow) w;
                    Draw.color(Tmp.c1.set(window.locked ? Color.orange : Color.darkGray).lerp(window.locked ? Color.scarlet : Color.gray, Mathf.absin(Time.time, 3f, 1f)).a(settings.getInt("selectopacity") / 100f));
                    float length = (window.target instanceof Unit u
                            ? u.hitSize
                            : window.target instanceof Building b
                                ? b.block.size * tilesize
                                : 0
                    ) * 1.5f + 2.5f;
                    for(int i = 0; i < 4; i++){
                        float rot = i * 90f + 45f + (-Time.time) % 360f;
                        Draw.rect("select-arrow", window.target.x() + Angles.trnsx(rot, length), window.target.y() + Angles.trnsy(rot, length), length / 1.9f, length / 1.9f, rot - 135f);
                    }
                });

                Draw.color();
            }

            if(settings.getBool("distanceLine")) {
                Posc from = player;
                Position to = getTarget();
                if(to == from || to == null) to = input.mouseWorld();
                if(player.unit() instanceof BlockUnitUnit bu) Tmp.v1.set(bu.x() + bu.tile().block.offset, bu.y() + bu.tile().block.offset).sub(to.getX(), to.getY()).limit(bu.tile().block.size * tilesize +  sin + 0.5f);
                else Tmp.v1.set(from.x(), from.y()).sub(to.getX(), to.getY()).limit((player.unit() == null ? 0 : player.unit().hitSize) +  sin + 0.5f);

                float x2 = from.x() - Tmp.v1.x, y2 = from.y() - Tmp.v1.y, x1 = to.getX() + Tmp.v1.x, y1 = to.getY() + Tmp.v1.y;
                int segs = (int) (to.dst(from.x(), from.y()) / tilesize);
                if(segs > 0){
                    Lines.stroke(2.5f, Pal.gray);
                    Lines.dashLine(x1, y1, x2, y2, segs);
                    Lines.stroke(1f, Pal.placing);
                    Lines.dashLine(x1, y1, x2, y2, segs);

                    Fonts.outline.draw(Strings.fixed(to.dst(from.x(), from.y()), 2) + " (" + segs + " " + bundle.get("tiles") + ")",
                            from.x() + Angles.trnsx(Angles.angle(from.x(), from.y(), to.getX(), to.getY()), player.unit().hitSize() + Math.min(segs, 6) * 8f),
                            from.y() + Angles.trnsy(Angles.angle(from.x(), from.y(), to.getX(), to.getY()), player.unit().hitSize() + Math.min(segs, 6) * 8f) - 3,
                            Pal.accent, 0.25f, false, Align.center);
                }
            }
            Draw.reset();
        });
    }
}
