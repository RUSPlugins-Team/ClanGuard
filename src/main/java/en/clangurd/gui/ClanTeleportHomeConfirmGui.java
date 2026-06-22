package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanTeleportHomeConfirmGui {

    public static final int FORM_ID = 1018;

    private ClanTeleportHomeConfirmGui() {
    }

    public static void open(Player player, String homeName) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_tp_home_confirm_title"),
                Lang.get("gui_tp_home_confirm_content", "home", homeName)
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_teleport")));
        window.addButton(new ElementButton(Lang.get("gui_btn_cancel")));
        player.showFormWindow(window, FORM_ID);
    }
}