package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlyCommand {

    @Command(names={"fly"}, permission="basic.staff", description = "Toggle a player's fly mode")
    public static void fly(Player sender, @Param(name="player", defaultValue="self") Player target) {
        if (!sender.equals(target) && !sender.hasPermission("basic.fly.other")) {
            sender.sendMessage(ChatColor.RED + "No permission to set other player's fly mode.");
            return;
        }
        target.setAllowFlight(!target.getAllowFlight());
        if (!sender.equals(target)) {
            sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + "'s fly mode was set to " + ChatColor.WHITE + target.getAllowFlight() + ChatColor.GOLD + ".");
        }
        target.sendMessage(ChatColor.GOLD + "Your fly mode was set to " + ChatColor.WHITE + target.getAllowFlight() + ChatColor.GOLD + ".");
    }
}

