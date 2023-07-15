package net.lugami.bridge.bukkit.commands.updater;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.bukkit.util.PluginUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class UpdaterUnloadCommand {

    @Command(names = "updater unload", permission = "bridge.updater", hidden = true, description = "Unloads a plugin")
    public static void load(CommandSender sender, @Param(name = "plugin", wildcard = true) Plugin plugin) {
        sender.sendMessage(PluginUtil.unload(plugin));
    }
}
