package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.clandata.ClanManager;
import en.clangurd.lang.Lang;

import java.util.List;

public final class ClanKickGui {

    public static final int FORM_ID = 1007;

    private ClanKickGui() {
    }

    public static void open(Player player, String clanName) {
        List<String> members = ClanManager.getClanMembers(clanName);
        if (members == null || members.isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_no_members_kick"));
            return;
        }

        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_kick_title"),
                Lang.get("gui_kick_content")
        );

        String leaderName = player.getName();
        for (String member : members) {
            if (!member.equalsIgnoreCase(leaderName)) {
                window.addButton(new ElementButton(member));
            }
        }

        if (window.getButtons().isEmpty()) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_no_members_kick"));
            return;
        }

        player.showFormWindow(window, FORM_ID);
    }
}
