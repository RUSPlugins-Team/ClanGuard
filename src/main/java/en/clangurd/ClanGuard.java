package en.clangurd;

import cn.nukkit.plugin.PluginBase;
import en.clangurd.command.ClanCommand;
import en.clangurd.lang.Lang;
import en.clangurd.listener.FormListener;

public final class ClanGuard extends PluginBase {

    private static ClanGuard instance;
    private static boolean economyEnabled = false;

    @Override
    public void onEnable() {
        instance = this;
        Lang.init();
        checkEconomy();
        getServer().getCommandMap().register("clanguard", new ClanCommand());
        getServer().getPluginManager().registerEvents(new FormListener(this), this);
        getLogger().info("ClanGuard version 1.0.0 powered by RUSPlugins-Team (Nukkit Plugin)");
    }

    @Override
    public void onDisable() {
    }

    private void checkEconomy() {
        if (getServer().getPluginManager().getPlugin("EconomyAPI") != null) {
            economyEnabled = true;
        } else {
            economyEnabled = false;
            getLogger().warning("EconomyAPI not found! Treasury functions will be disabled.");
        }
    }

    public static ClanGuard getInstance() {
        return instance;
    }

    public static boolean isEconomyEnabled() {
        return economyEnabled;
    }
}
