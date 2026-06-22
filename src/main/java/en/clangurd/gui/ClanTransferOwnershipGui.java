package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

import java.util.List;

public final class ClanTransferOwnershipGui {

    public static final int FORM_ID = 1019;

    private ClanTransferOwnershipGui() {
    }

    public static void open(Player player, List<String> candidates) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_transfer_title"),
                Lang.get("gui_transfer_content")
        );
        for (String candidate : candidates) {
            window.addButton(new ElementButton(candidate));
        }
        player.showFormWindow(window, FORM_ID);
    }
}