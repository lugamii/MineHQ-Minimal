package net.lugami.bridge.bukkit.commands.updater;

import net.lugami.qlib.command.Command;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;

public class UpdaterInfoCommand {

    @Command(names = "updater info", permission = "bridge.updater", hidden = true, description = "Check information about the updater.")
    public static void info(CommandSender sender) {
        sender.sendMessage(BukkitAPI.LINE);
        sender.sendMessage("§6§lUpdater §7❘ §fInformation");
        sender.sendMessage(BukkitAPI.LINE);
        sender.sendMessage(ChatColor.YELLOW + "Updating for Group: " + ChatColor.WHITE + StringUtils.join(BridgeGlobal.getUpdaterGroups(), ", ") + " & Global");
        sender.sendMessage(ChatColor.YELLOW + "Using Root Directory: " + ChatColor.WHITE + BridgeGlobal.getUpdaterManager().getPluginUpdateDir());

        List<File> fileList = BridgeGlobal.getUpdaterManager().getFilesForGroup(BridgeGlobal.getUpdaterGroups());
        sender.sendMessage(ChatColor.YELLOW + "Plugins available: " + ChatColor.WHITE + (fileList == null || fileList.isEmpty() ? "N/A" : fileList.size()));
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Use /updater list to list all available plugins...");
        sender.sendMessage(BukkitAPI.LINE);
    }
}
