package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import en.clangurd.lang.Lang;

public final class ClanTreasuryCustomDepositGui {

    public static final int FORM_ID = 1011;

    private ClanTreasuryCustomDepositGui() {
    }

    public static void open(Player player) {
        FormWindowCustom window = new FormWindowCustom(Lang.get("gui_deposit_custom_title"));
        window.addElement(new ElementLabel(Lang.get("gui_deposit_custom_content")));
        window.addElement(new ElementInput(Lang.get("gui_deposit_input"), Lang.get("gui_deposit_hint")));
        player.showFormWindow(window, FORM_ID);
    }
}
