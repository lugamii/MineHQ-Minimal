package net.lugami.bridge.bukkit.commands.updater;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.updater.UpdateStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class UpdaterListCommand {

    @Command(names = "updater list", permission = "bridge.updater", hidden = true, description = "List all files in your group")
    public static void list(CommandSender sender, @Param(name = "group", defaultValue = "current") String groupName) {
        List<File> files = BridgeGlobal.getUpdaterManager().getFilesForGroup((groupName.equals("current") ? BridgeGlobal.getUpdaterGroups() : Collections.singletonList(groupName)));
        sender.sendMessage(BukkitAPI.LINE);
        sender.sendMessage("§6§lUpdater §7❘ §fList");
        sender.sendMessage(BukkitAPI.LINE);
        if (files == null) {
            sender.sendMessage(ChatColor.RED + "The directory for the group does not exist...");
        } else if (files.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No available plugins for this group...");
        } else {
            files.forEach(file -> {
                UpdateStatus updateStatus = BridgeGlobal.getUpdaterManager().getStatus(file);
                sender.sendMessage(ChatColor.YELLOW + file.getName() + ChatColor.GRAY + " (" + file.getPath() + ")" + (file.getAbsolutePath().contains("Global") ? ChatColor.RED + " [Global]" : "") + ' ' + updateStatus.getPrefix());
            });
        }
        sender.sendMessage(BukkitAPI.LINE);

    }
}
