package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import en.clangurd.lang.Lang;

public final class ClanDescriptionGui {

    public static final int FORM_ID = 1003;

    private ClanDescriptionGui() {
    }

    public static void open(Player player) {
        FormWindowCustom window = new FormWindowCustom(Lang.get("gui_description_title"));
        window.addElement(new ElementLabel(Lang.get("gui_description_content")));
        window.addElement(new ElementInput(Lang.get("gui_description_input"), Lang.get("gui_description_hint")));
        player.showFormWindow(window, FORM_ID);
    }
}
