package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanDeleteGui {

    public static final int FORM_ID = 1015;

    private ClanDeleteGui() {
    }

    public static void open(Player player, String clanName, String clanTag) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_delete_title"),
                Lang.get("gui_delete_content", "name", clanName, "tag", clanTag)
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_delete_confirm")));
        window.addButton(new ElementButton(Lang.get("gui_btn_cancel")));
        player.showFormWindow(window, FORM_ID);
    }
}
