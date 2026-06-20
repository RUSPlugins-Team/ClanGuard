package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import en.clangurd.lang.Lang;

public final class ClanChatGui {

    public static final int FORM_ID = 1014;

    private ClanChatGui() {
    }

    public static void open(Player player, String lastMessageRole, String lastMessageNick, String lastMessage) {
        FormWindowCustom window = new FormWindowCustom(Lang.get("gui_chat_title"));

        String lastMsgDisplay;
        if (lastMessage == null || lastMessage.isEmpty()) {
            lastMsgDisplay = Lang.get("gui_chat_no_messages");
        } else {
            lastMsgDisplay = "\u00A7e[" + lastMessageRole + "] \u00A7b" + lastMessageNick + "\u00A7f: " + lastMessage;
        }

        window.addElement(new ElementLabel(Lang.get("gui_chat_content", "lastmsg", lastMsgDisplay)));
        window.addElement(new ElementInput(Lang.get("gui_chat_input"), Lang.get("gui_chat_hint")));
        player.showFormWindow(window, FORM_ID);
    }
}
