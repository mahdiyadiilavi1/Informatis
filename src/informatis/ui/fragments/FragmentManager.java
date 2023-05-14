package informatis.ui.fragments;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;

import static arc.Core.scene;
import static mindustry.Vars.ui;

public class FragmentManager {
    public static void init() {
        //layout debug
        Seq.with(scene.root,
                ui.picker, ui.editor, ui.controls, ui.restart, ui.join, ui.discord,
                ui.load, ui.custom, ui.language, ui.database, ui.settings, ui.host,
                ui.paused, ui.about, ui.bans, ui.admins, ui.traces, ui.map, ui.content,
                ui.planet, ui.research, ui.mods, ui.schematics, ui.logic
        ).each(dialog-> dialog.addChild(new ElementViewFragment(dialog)));

        new QuickSchemFragment();
        new TileInfoFragment();
        new ServerSearchFragment();
    }
}
