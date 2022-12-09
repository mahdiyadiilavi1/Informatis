package informatis.ui.managers;

import arc.scene.event.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.ctype.ContentType;
import mindustry.entities.units.*;

import arc.struct.Queue;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class BuildNoteManager extends BaseManager {
    private final Seq<Queue<BuildPlan>> plans = new Seq<>(10);
    private static BuildNoteManager instance;


    private BuildNoteManager() {
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
                        listItem.table(stack -> stack.add(buildListItem(finalI)).grow().width(80))
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
    public static BuildNoteManager getInstance() {
        if(instance == null) instance = new BuildNoteManager();
        return instance;
    }

    private Queue<BuildPlan> copyPlan(Queue<BuildPlan> target) {
        Queue<BuildPlan> copied = new Queue<>(target.size);
        for(BuildPlan plan : target) copied.add(plan.copy());
        return copied;
    }

    public void updateTrooping(int index) {
        plans.set(index, copyPlan(Vars.player.unit().plans()));
        rerenderListItem();
    }
    public void selectBuildPlan(int index) {
        for(BuildPlan plan : plans.get(index)) {
            Vars.player.unit().addBuild(plan.copy());
        }
    }
    private Table buildListItem(int index) {
        Table table = new Table();
        IntSet cache = new IntSet();
        for(BuildPlan plan : plans.get(index)) {
            Block block = plan.block;
            if(cache.contains(block.id)) continue;
            cache.add(block.id);
            if(cache.size >= 5) continue;
            table.image(block.uiIcon).size(16);
        }
        if(cache.size >= 5) {
            table.row();
            table.add("...").center().growX().tooltip(toolt -> {
                toolt.add();
                int[] cs = cache.iterator().toArray().toArray();
                for(int id : cs) {
                    toolt.image(((Block) Vars.content.getByID(ContentType.block, id)).uiIcon).size(16);
                }
            });
        }
        return table;
    }
    private void rerenderListItem() {
        for(int i = 0; i < 10; i++) {
            Table table = body.find("listItem-"+i);
            table.clear();
            table.add(buildListItem(i));
        }
    }
}
