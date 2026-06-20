package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import en.clangurd.lang.Lang;

public final class ClanTreasuryCustomWithdrawGui {

    public static final int FORM_ID = 1013;

    private ClanTreasuryCustomWithdrawGui() {
    }

    public static void open(Player player, long currentTreasury) {
        FormWindowCustom window = new FormWindowCustom(Lang.get("gui_withdraw_custom_title"));
        window.addElement(new ElementLabel(Lang.get("gui_withdraw_custom_content", "treasury", formatMoney(currentTreasury))));
        window.addElement(new ElementInput(Lang.get("gui_deposit_input"), Lang.get("gui_deposit_hint")));
        player.showFormWindow(window, FORM_ID);
    }

    private static String formatMoney(long amount) {
        return String.format("%,d", amount);
    }
}
