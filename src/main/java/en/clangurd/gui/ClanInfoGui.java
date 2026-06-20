package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.clandata.ClanManager;
import en.clangurd.lang.Lang;

import java.util.Map;

public final class ClanInfoGui {

    public static final int FORM_ID = 1004;

    private ClanInfoGui() {
    }

    public static void open(Player player, String clanName) {
        Map<String, Object> info = ClanManager.getClanInfo(clanName);
        if (info == null) {
            player.sendMessage(Lang.get("prefix") + Lang.get("error_failed"));
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append(Lang.get("gui_info_leader", "value", String.valueOf(info.get("leader")))).append("\n\n");
        content.append(Lang.get("gui_info_tag", "value", String.valueOf(info.get("tag")))).append("\n\n");
        content.append(Lang.get("gui_info_description", "value", String.valueOf(info.get("description")))).append("\n\n");
        content.append(Lang.get("gui_info_members", "count", String.valueOf(info.get("count")), "max", String.valueOf(info.get("maxPlayers")))).append("\n\n");
        content.append(Lang.get("gui_info_treasury", "value", String.valueOf(info.get("treasury")))).append("\n\n");
        content.append(Lang.get("gui_info_rank", "value", String.valueOf(info.get("rank")))).append("\n");
        content.append(Lang.get("gui_info_title_clan", "value", String.valueOf(info.get("title")))).append("\n");
        content.append(Lang.get("gui_info_level", "value", String.valueOf(info.get("level")))).append("\n\n");
        content.append(Lang.get("gui_info_regions", "value", String.valueOf(info.get("regions")))).append("\n");
        content.append(Lang.get("gui_info_homes", "value", String.valueOf(info.get("homePoints")))).append("\n\n");
        content.append(Lang.get("gui_info_stats", "wins", String.valueOf(info.get("wins")), "losses", String.valueOf(info.get("losses")), "deaths", String.valueOf(info.get("deaths")))).append("\n\n");
        content.append(Lang.get("gui_info_created", "value", String.valueOf(info.get("createdAt"))));

        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_info_title", "name", clanName),
                content.toString()
        );
        window.addButton(new ElementButton(Lang.get("gui_btn_close")));
        player.showFormWindow(window, FORM_ID);
    }
}
