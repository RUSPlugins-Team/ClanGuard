package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanTransferOwnershipConfirmGui {

    public static final int FORM_ID = 1020;

    private ClanTransferOwnershipConfirmGui() {
    }

    public static void open(Player player, String targetName) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_transfer_confirm_title"),
                Lang.get("gui_transfer_confirm_content", "player", targetName)
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_transfer")));
        window.addButton(new ElementButton(Lang.get("gui_btn_cancel")));
        player.showFormWindow(window, FORM_ID);
    }
}