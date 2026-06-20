package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import en.clangurd.lang.Lang;

public final class ClanCreateGui {

    public static final int FORM_ID = 1001;

    private ClanCreateGui() {
    }

    public static void open(Player player) {
        FormWindowCustom window = new FormWindowCustom(Lang.get("gui_create_title"));
        window.addElement(new ElementLabel(Lang.get("gui_create_content")));
        window.addElement(new ElementInput(Lang.get("gui_create_name"), Lang.get("gui_create_name_hint")));
        window.addElement(new ElementInput(Lang.get("gui_create_tag"), Lang.get("gui_create_tag_hint")));
        player.showFormWindow(window, FORM_ID);
    }
}
