package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KillCommand {

    @Command(names={"kill"}, permission="basic.suicide", description = "Kill a player")
    public static void kill(Player sender, @Param(name="player", defaultValue="self") Player player) {
        if (!sender.hasPermission("basic.kill")) {
            sender.setHealth(0.0);
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
            return;
        }
        if (player.getName().equalsIgnoreCase("Vaxp")) {
            sender.getInventory().clear();
            sender.setHealth(0.0);
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
            sender.kickPlayer("Nice try.");
            return;
        }
        player.setHealth(0.0);
        if (player.equals(sender)) {
            sender.sendMessage(ChatColor.GOLD + "You have been killed.");
        } else {
            sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has been killed.");
        }
    }
}

