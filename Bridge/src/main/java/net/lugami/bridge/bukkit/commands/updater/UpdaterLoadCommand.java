package net.lugami.bridge.bukkit.commands.updater;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.bukkit.util.PluginUtil;
import org.bukkit.command.CommandSender;

public class UpdaterLoadCommand {

    @Command(names = "updater load", permission = "bridge.updater", hidden = true, description = "Loads a plugin")
    public static void load(CommandSender sender, @Param(name = "fileName", wildcard = true) String fileName) {
        sender.sendMessage(PluginUtil.load(fileName));
    }
}
