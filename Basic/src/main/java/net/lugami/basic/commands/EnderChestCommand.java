package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnderChestCommand {

    @Command(names={"enderchest", "echest"}, permission="basic.enderchest", description = "Open a player's Ender Chest")
    public static void enderchest(Player sender, @Param(name="player", defaultValue="self") Player player) {
        if (!player.getUniqueId().equals(sender.getUniqueId()) && !sender.hasPermission("basic.enderchest.other")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to open other players' Ender Chests!");
            return;
        }
        sender.openInventory(player.getEnderChest());
    }
}

