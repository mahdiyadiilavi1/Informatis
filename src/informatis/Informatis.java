package informatis;

import arc.*;
import arc.input.*;

import informatis.core.*;
import informatis.draws.*;
import informatis.ui.*;
import informatis.ui.managers.*;
import informatis.ui.windows.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import static arc.Core.*;

public class Informatis extends Mod {
    @Override
    public void init(){
        Core.app.post(() -> {
            Mods.ModMeta meta = Vars.mods.locateMod("informatis").meta;
            meta.displayName = "[#B5FFD9]Informatis[]";
            meta.author = "[#B5FFD9]Sharlotte[lightgray]#0018[][]";
            meta.description = bundle.get("shar-description");
        });

        Events.run(Trigger.update, () -> {
            if((input.keyDown(KeyCode.shiftRight) || input.keyDown(KeyCode.shiftLeft))) {
                if(input.keyTap(KeyCode.r)) {
                    UnitWindow.currentWindow.locked = !UnitWindow.currentWindow.locked;
                }
            }
            int i = 0;
            for(KeyCode numCode : KeyCode.numbers) {
                if(input.keyTap(numCode)) {
                    if (Vars.control.input.commandMode) {
                        if (input.keyDown(KeyCode.altLeft)) TroopingManager.getInstance().applyTrooping(i);
                        else if (input.keyDown(KeyCode.capsLock)) TroopingManager.getInstance().updateTrooping(i);
                        else TroopingManager.getInstance().selectTrooping(i);
                    } else {
                        if (input.keyDown(KeyCode.capsLock)) BuildNoteManager.getInstance().updateTrooping(i);
                        else BuildNoteManager.getInstance().selectBuildPlan(i);
                    }
                    break;
                }
                i++;
            }
        });

        Events.on(ClientLoadEvent.class, e -> {
            Setting.init();
            OverDraws.init();
            OverDrawer.init();
            SVars.init();
            Vars.ui.hudGroup.addChild(
                new SidebarSwitcher(
                    WindowManager.getInstance().body,
                    DialogManager.getInstance().body,
                    TroopingManager.getInstance().body,
                    BuildNoteManager.getInstance().body,
                    FragmentManager.getInstance().body
                )
            );
        });
    }
}
