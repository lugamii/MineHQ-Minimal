package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FeedCommand {

    @Command(names={"feed"}, permission="basic.feed", description = "Feed a player")
    public static void feed(Player sender, @Param(name="player", defaultValue="self") Player target) {
        if (!sender.equals(target) && !sender.hasPermission("basic.feed.other")) {
            sender.sendMessage(ChatColor.RED + "No permission to feed other players.");
            return;
        }
        target.setFoodLevel(20);
        target.setSaturation(10.0f);
        if (!sender.equals(target)) {
            sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + " has been fed.");
        }
        target.sendMessage(ChatColor.GOLD + "You have been fed.");
    }
}

