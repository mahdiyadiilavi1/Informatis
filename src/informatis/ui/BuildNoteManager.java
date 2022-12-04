package informatis.ui;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;

import arc.struct.Queue;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class BuildNoteManager {
    public static Table body;
    private static Seq<Queue<BuildPlan>> plans = new Seq<>(10);

    public static void init() {
        for(int i = 0; i < 10; i++) plans.add(new Queue<>());
        body = new Table(t -> {
            t.defaults().growX();

            t.table(list -> {
                for(int i = 0; i < 10; i++) {
                    final int finalI = i;
                    Table listItem = new Table();
                    listItem.left();
                    listItem.add(String.valueOf(i)).fontScale(0.75f).width(15).padRight(30);
                    listItem.clicked(() -> selectBuildPlan(finalI));
                    listItem.table(stack -> stack.add(buildListItem(finalI))).marginLeft(20).marginRight(20).name("listItem-"+i).grow();
                    listItem.table(icons -> {
                        icons.image(Icon.cancelSmall).size(20).color(Pal.health).padLeft(10).grow().get().clicked(() -> {
                            plans.get(finalI).clear();
                            rerenderListItem(finalI);
                        });
                        icons.image(Icon.upSmall).size(20).color(Pal.heal).padLeft(10).grow().get().clicked(() -> {
                            plans.set(finalI, Vars.player.unit().plans());
                            rerenderListItem(finalI);
                        });
                    }).padLeft(10).width(80).grow();
                    list.add(listItem).pad(10).grow().row();
                    list.image().height(2f).color(Pal.gray).grow().row();
                }
            });
        });
    }

    public static void selectBuildPlan(int index) {
        Vars.player.unit().plans(plans.get(index));
    }
    private static Stack buildListItem(int index) {
        return new Stack() {{
            IntSet cache = new IntSet();
            for(int i = 0; i < plans.get(index).size && cache.size < 5; i++) {
                final int fianlI = i;
                Block block = plans.get(index).get(i).block;
                if(cache.contains(block.id)) continue;
                cache.add(block.id);
                Log.info(cache);

                add(new Table(icon -> {
                    icon.right();
                    icon.add(new Image() {
                        {
                            setDrawable(block.uiIcon);
                        }

                        @Override
                        public void draw() {
                            super.draw();

                            int size = 4;
                            Lines.stroke(Scl.scl(2f), Pal.gray.cpy().a(parentAlpha));
                            Lines.rect(x - size / 2f, y - size / 2f, width + size, height + size);
                            Draw.reset();
                        }
                    }).size(16).padRight(fianlI * 4);
                }));
            }
        }};
    }
    private static void rerenderListItem(int index) {
        Table table = body.find("listItem-"+index);
        table.clear();
        table.add(buildListItem(index));
    }
}
