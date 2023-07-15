package net.lugami.bridge.bukkit.commands.updater;

import net.lugami.bridge.bukkit.commands.updater.menu.UpdaterMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.BridgeGlobal;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class UpdaterUpdateCommand {

    @Command(names = "updater update", permission = "bridge.updater", hidden = true, description = "Update specific plugins")
    public static void update(CommandSender sender, @Param(name = "pluginname", defaultValue = "none") String pluginName) {
        List<String> group = BridgeGlobal.getUpdaterGroups();
        List<File> files = BridgeGlobal.getUpdaterManager().getFilesForGroup(group);
        switch (pluginName) {
            case "all": {
                BridgeGlobal.getUpdaterManager().updatePlugins(files, cons -> sender.sendMessage(ChatColor.BLUE + cons));
                break;
            }

            case "none": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Sorry ingame only!");
                    return;
                }
                new UpdaterMenu(group).openMenu((Player) sender);
                break;
            }

            default: {
                File pluginFile = files.stream().filter(file -> file.getName().equalsIgnoreCase(pluginName)).findAny().orElse(null);
                if (pluginFile == null) {
                    sender.sendMessage(ChatColor.RED + "There is no such plugin file by the name \"" + pluginName + "\", use /updater list to get a list of plugins.");
                    return;
                }

                sender.sendMessage(ChatColor.GREEN + "Attempting to update " + pluginFile.getName() + "!");
                BridgeGlobal.getUpdaterManager().updatePlugins(Collections.singletonList(pluginFile), cons -> sender.sendMessage(ChatColor.BLUE + cons));
            }

        }
    }
}
