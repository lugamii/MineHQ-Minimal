package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RawCommands {

    @Command(names={"raw"}, permission="basic.raw", description = "Broadcast a raw message. Supports color codes")
    public static void raw(CommandSender sender, @Param(name="message", defaultValue=" ", wildcard=true) String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Command(names={"msgraw"}, permission="basic.raw", description = "Send a raw message to a player. Supports color codes")
    public static void msgraw(CommandSender sender, @Param(name="player") Player target, @Param(name="message", defaultValue=" ", wildcard=true) String message) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}

