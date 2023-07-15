package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand {

    @Command(names={"clear", "ci", "clearinv"}, permission="basic.clear", description = "Clear a player's inventory")
    public static void clear(CommandSender sender, @Param(name="player", defaultValue="self") Player target) {
        if (!sender.equals(target) && !sender.hasPermission("basic.clear.other")) {
            sender.sendMessage(ChatColor.RED + "No permission to clear other player's inventories.");
            return;
        }
        target.getInventory().clear();
        target.getInventory().setArmorContents((ItemStack[])null);
        if (!sender.equals(target)) {
            sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + "'s inventory has been cleared.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Your inventory has been cleared.");
        }
    }
}

