package en.clangurd.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import en.clangurd.gui.ClanMainGui;
import en.clangurd.lang.Lang;

public final class ClanCommand extends Command {

    public ClanCommand() {
        super("clan", "Open the ClanGuard panel", "/clan");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.get("error_console_only"));
            return true;
        }
        Player player = (Player) sender;
        ClanMainGui.open(player);
        return true;
    }
}
