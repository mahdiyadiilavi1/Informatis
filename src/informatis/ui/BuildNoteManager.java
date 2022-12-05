package informatis.ui;

import arc.graphics.g2d.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.units.*;

import arc.struct.Queue;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Build;

public class BuildNoteManager {
    public static Table body;
    private static final Seq<Queue<BuildPlan>> plans = new Seq<>(10);

    public static void init() {
        for(int i = 0; i < 10; i++) plans.add(new Queue<>());
        body = new Table(t -> {
            t.defaults().growX();

            t.table(list -> {
                list.defaults().grow();

                for(int i = 1; i <= 10; i++) {
                    final int finalI = i % 10;
                    list.table(listItem -> {
                        listItem.touchable = Touchable.enabled;
                        listItem.clicked(() -> selectBuildPlan(finalI));
                        listItem.left().defaults().grow();

                        listItem.add(String.valueOf(finalI))
                                .fontScale(0.75f)
                                .width(15).padRight(30);
                        listItem.table(stack -> stack.add(buildListItem(finalI)).grow())
                                .width(80).padLeft(10)
                                .name("listItem-"+finalI);
                        listItem.image(Icon.cancelSmall)
                                .color(Pal.health)
                                .size(20).padLeft(10)
                                .get().clicked(() -> {
                                    plans.get(finalI).clear();
                                    rerenderListItem();
                                });
                        listItem.image(Icon.upSmall)
                                .color(Pal.heal)
                                .size(20).padLeft(10)
                                .get().clicked(() -> updateTrooping(finalI));
                    }).pad(10).row();
                    list.image().height(2f).color(Pal.gray).row();
                }
            });
        });
    }

    private static Queue<BuildPlan> copyPlan(Queue<BuildPlan> target) {
        Queue<BuildPlan> copied = new Queue<>(target.size);
        for(BuildPlan plan : target) copied.add(plan.copy());
        return copied;
    }

    public static void updateTrooping(int index) {
        plans.set(index, copyPlan(Vars.player.unit().plans()));
        rerenderListItem();
    }
    public static void selectBuildPlan(int index) {
        for(BuildPlan plan : plans.get(index)) {
            Vars.player.unit().addBuild(plan.copy());
        }
    }
    private static Stack buildListItem(int index) {
        Stack stack = new Stack();
        IntSet cache = new IntSet();
        for(BuildPlan plan : plans.get(index)) {
            Block block = plan.block;
            if(cache.contains(block.id)) continue;
            if(cache.size >= 5) break;
            cache.add(block.id);

            stack.add(new Table(iconTable -> {
                iconTable.right();
                iconTable.add(new Image(block.uiIcon) {
                    @Override
                    public void draw() {
                        super.draw();

                        int size = 4;
                        Lines.stroke(Scl.scl(2f), Pal.gray.cpy().a(parentAlpha));
                        Lines.rect(x - size / 2f, y - size / 2f, width + size, height + size);
                        Draw.reset();
                    }
                }).size(16).padRight((cache.size - 1) * 7);
            }));
        }
        return stack;
    }
    private static void rerenderListItem() {
        for(int i = 0; i < 10; i++) {
            Table table = body.find("listItem-"+i);
            table.clear();
            table.add(buildListItem(i));
        }
    }
}
