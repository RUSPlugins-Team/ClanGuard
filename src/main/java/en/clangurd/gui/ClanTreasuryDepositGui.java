package en.clangurd.gui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import en.clangurd.lang.Lang;

public final class ClanTreasuryDepositGui {

    public static final int FORM_ID = 1010;

    private ClanTreasuryDepositGui() {
    }

    public static void open(Player player, long currentTreasury) {
        FormWindowSimple window = new FormWindowSimple(
                Lang.get("gui_deposit_title"),
                Lang.get("gui_deposit_content", "amount", formatMoney(currentTreasury))
        );
        window.addButton(new ElementButton("10,000"));
        window.addButton(new ElementButton("50,000"));
        window.addButton(new ElementButton("100,000"));
        window.addButton(new ElementButton("500,000"));
        window.addButton(new ElementButton("1,000,000"));
        window.addButton(new ElementButton("5,000,000"));
        window.addButton(new ElementButton("10,000,000"));
        window.addButton(new ElementButton(Lang.get("gui_btn_custom_amount")));
        player.showFormWindow(window, FORM_ID);
    }

    private static String formatMoney(long amount) {
        return String.format("%,d", amount);
    }
}
