package informatis.ui.managers;

import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import informatis.ui.dialogs.ResourcePreviewDialog;
import mindustry.gen.Icon;

public class DialogManager extends BaseManager {
    public Dialog resourcePreview;
    private static DialogManager instance;
    private DialogManager() {
        resourcePreview = new ResourcePreviewDialog();

        body = new Table(t -> {
            t.top();
            t.button(Icon.file, () -> {
                resourcePreview.show();
            });
        });
    }

    public static DialogManager getInstance() {
        if(instance == null) instance = new DialogManager();
        return instance;
    }
}
