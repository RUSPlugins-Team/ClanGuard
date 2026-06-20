package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanKickConfirmGui {

    public static final int FORM_ID = 1008;

    private ClanKickConfirmGui() {
    }

    public static void open(Player player, String memberName) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_kick_confirm_title"),
                Lang.get("gui_kick_confirm_content", "player", memberName)
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_kick_confirm")));
        window.addButton(new ElementButton(Lang.get("gui_btn_cancel")));
        player.showFormWindow(window, FORM_ID);
    }
}
