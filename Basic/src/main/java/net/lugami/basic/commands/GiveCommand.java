package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {

    @Command(names={"item", "i", "get"}, permission="basic.give", description = "Spawn yourself an item")
    public static void item(Player sender, @Param(name="item") ItemStack item, @Param(name="amount", defaultValue="1") int amount) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return;
        }
        item.setAmount(amount);
        sender.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.WHITE + amount + ChatColor.GOLD + " of " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + ".");
    }

    @Command(names={"give"}, permission="basic.give.other", description = "Spawn a player an item")
    public static void give(Player sender, @Param(name="player") Player target, @Param(name="item") ItemStack item, @Param(name="amount", defaultValue="1") int amount) {
        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return;
        }
        item.setAmount(amount);
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GOLD + "Giving " + ChatColor.WHITE + target.getDisplayName() + ChatColor.WHITE + " " + amount + ChatColor.GOLD + " of " + ChatColor.WHITE + ItemUtils.getName(item) + ChatColor.GOLD + ".");
    }
}

