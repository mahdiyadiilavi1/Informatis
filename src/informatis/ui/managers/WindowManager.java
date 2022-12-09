package informatis.ui.managers;

import arc.*;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import informatis.ui.windows.*;
import mindustry.*;
import mindustry.graphics.Pal;
import mindustry.ui.*;

import java.lang.reflect.InvocationTargetException;

public class WindowManager extends BaseManager {
    public static ObjectMap<Class<? extends Window>, Seq<Window>> windows = new ObjectMap<>();
    private static WindowManager instance;

    public MapEditorWindow editorTable;

    private WindowManager(){
        new UnitWindow();
        new WaveWindow();
        new CoreWindow();
        new PlayerWindow();
        new ToolWindow();
        editorTable = new MapEditorWindow();
        for(Seq<Window> windows : windows.values()){
            for(Window window : windows) {
                window.build();
            }
        }

        // windows place for dragging
        Vars.ui.hudGroup.fill(t -> {
            t.name = "Windows";

            for(Seq<Window> windows : windows.values()){
                for(Window window : windows) {
                    t.add(window).height(window.getHeight()).width(window.getWidth());
                }
            }
        });

        // windows sidebar -> will be moved to sidebar switch button
        body = new Table(t -> {
            t.name = "Window Buttons";
            t.left();

            for(ObjectMap.Entry<Class<? extends Window>, Seq<Window>> windows : windows){
                Class<? extends Window> key = windows.key;
                Seq<Window> value = windows.value;
                Window window = value.peek();
                t.stack(
                    new Table(bt -> {
                        bt.button(window.icon, Styles.emptyi, () -> {
                            Window window1 = window;
                            window1.parent.setLayoutEnabled(false);
                            if(!window.only) {
                                try {
                                    window1 = key.getConstructor().newInstance();
                                    window1.setPosition(value.peek().x + 50, value.peek().y + 50);
                                    window1.sizeBy(200, 200);
                                } catch (InstantiationException | IllegalAccessException |
                                         InvocationTargetException | NoSuchMethodException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            window1.toggle();
                            for (Seq<Window> ws : WindowManager.windows.values()) {
                                ws.each(w -> w.setLayoutEnabled(true));
                            }
                        }).size(40f).tooltip(tt -> {
                            tt.setBackground(Styles.black6);
                            tt.label(() -> Core.bundle.get("window."+window.name+".name")).pad(2f);
                        });
                    }),
                    new Table(bt -> {
                        bt.right().bottom();
                        if(!window.only) bt.label(() -> String.valueOf(value.size)).get().setColor(Pal.accent);
                    })
                ).row();
            }
        }).left();
    }

    public static WindowManager getInstance() {
        if(instance == null) instance = new WindowManager();
        return instance;
    }

    public static void register(Window window){
        if(!windows.containsKey(window.getClass())) windows.put(window.getClass(), Seq.with(window));
        else windows.get(window.getClass()).add(window);
    }
}
