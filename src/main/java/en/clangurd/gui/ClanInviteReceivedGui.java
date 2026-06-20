package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanInviteReceivedGui {

    public static final int FORM_ID = 1006;

    private ClanInviteReceivedGui() {
    }

    public static void open(Player player, String leaderName, String clanName, String clanTag) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_invite_received_title"),
                Lang.get("gui_invite_received_content", "tag", clanTag, "name", clanName, "leader", leaderName)
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_join")));
        window.addButton(new ElementButton(Lang.get("gui_btn_decline")));
        player.showFormWindow(window, FORM_ID);
    }
}
