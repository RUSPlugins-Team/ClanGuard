package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

import java.util.List;

public final class ClanTeleportHomeListGui {

    public static final int FORM_ID = 1017;

    private ClanTeleportHomeListGui() {
    }

    public static void open(Player player, List<String> homeNames) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_tp_home_title"),
                Lang.get("gui_tp_home_content")
        );
        for (String homeName : homeNames) {
            window.addButton(new ElementButton(homeName));
        }
        player.showFormWindow(window, FORM_ID);
    }
}