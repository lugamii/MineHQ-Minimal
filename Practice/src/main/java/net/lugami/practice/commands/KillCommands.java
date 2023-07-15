package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class KillCommands {

    @Command(names = {"suicide"}, permission = "")
    public static void suicide(Player sender) {
        sender.sendMessage(ChatColor.RED + "/suicide has been disabled.");
    }

    @Command(names = {"kill"}, permission = "basic.kill")
    public static void kill(Player sender, @Param(name="target") Player target) {
        target.setHealth(0);
        sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + " has been killed.");
    }

}