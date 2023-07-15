package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand {
    @Command(names={"say", "broadcast", "bc"}, permission="basic.say", description = "Broadcast a message to the server. Supports color codes")
    public static void say(CommandSender sender, @Param(name="message", wildcard=true) String message) {
        String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : sender.getName();
        message = ChatColor.translateAlternateColorCodes('&', ("&d[" + senderName + "&d] " + message));
        Bukkit.broadcastMessage(message);
    }
}

