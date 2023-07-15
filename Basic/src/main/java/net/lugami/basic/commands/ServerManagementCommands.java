package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerManagementCommands {

    @Command(names={"freezeserver"}, permission="basic.freezeserver", description = "Freeze the server. Normal players won't be able to move or interact")
    public static void freezeserver(CommandSender sender) {
        Basic.getInstance().getServerManager().setFrozen(!Basic.getInstance().getServerManager().isFrozen());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("basic.staff")) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "The server has been " + (Basic.getInstance().getServerManager().isFrozen() ? "" : "un") + "frozen by " + sender.getName() + ".");
                continue;
            }
            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "The server has been " + (Basic.getInstance().getServerManager().isFrozen() ? "" : "un") + "frozen.");
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "The server has been " + (Basic.getInstance().getServerManager().isFrozen() ? "" : "un") + "frozen by " + sender.getName() + ".");
    }
}

