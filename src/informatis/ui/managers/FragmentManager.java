package informatis.ui.managers;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import informatis.ui.fragments.ElementViewFragment;
import informatis.ui.fragments.QuickSchemFragment;
import informatis.ui.fragments.ServerSearchFragment;
import informatis.ui.fragments.TileInfoFragment;

import static arc.Core.scene;
import static mindustry.Vars.ui;

public class FragmentManager extends BaseManager {
    private static FragmentManager instance;

    private FragmentManager() {
        //layout debug
        Seq.with(scene.root,
                ui.picker, ui.editor, ui.controls, ui.restart, ui.join, ui.discord,
                ui.load, ui.custom, ui.language, ui.database, ui.settings, ui.host,
                ui.paused, ui.about, ui.bans, ui.admins, ui.traces, ui.maps, ui.content,
                ui.planet, ui.research, ui.mods, ui.schematics, ui.logic
        ).each(dialog-> dialog.addChild(new ElementViewFragment(dialog)));

        new QuickSchemFragment();
        new TileInfoFragment();
        new ServerSearchFragment();

        body = new Table();
    }

    public static FragmentManager getInstance() {
        if(instance == null) instance = new FragmentManager();
        return instance;
    }
}
