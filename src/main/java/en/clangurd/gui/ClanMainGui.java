package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanMainGui {

    public static final int FORM_ID = 1000;

    private ClanMainGui() {
    }

    public static void open(Player player) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_main_title"),
                Lang.get("gui_main_content")
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_create")));
        window.addButton(new ElementButton(Lang.get("gui_btn_info")));
        window.addButton(new ElementButton(Lang.get("gui_btn_invite")));
        window.addButton(new ElementButton(Lang.get("gui_btn_kick")));
        window.addButton(new ElementButton(Lang.get("gui_btn_deposit")));
        window.addButton(new ElementButton(Lang.get("gui_btn_withdraw")));
        window.addButton(new ElementButton(Lang.get("gui_btn_chat")));
        window.addButton(new ElementButton(Lang.get("gui_btn_set_home")));
        window.addButton(new ElementButton(Lang.get("gui_btn_tp_home")));
        window.addButton(new ElementButton(Lang.get("gui_btn_transfer_owner")));
        window.addButton(new ElementButton(Lang.get("gui_btn_rename")));
        window.addButton(new ElementButton(Lang.get("gui_btn_description")));
        window.addButton(new ElementButton(Lang.get("gui_btn_delete")));
        player.showFormWindow(window, FORM_ID);
    }
}
