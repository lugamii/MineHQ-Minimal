package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConfigReloadCommand {

    @Command(names = {"practice reload"}, permission = "op", hidden = true)
    public static void config(CommandSender sender) {

        Practice.getInstance().reloadConfig();
        Practice.getInstance().saveConfig();
        sender.sendMessage(ChatColor.GREEN + "Practice Configuration Reloaded.");
    }
}