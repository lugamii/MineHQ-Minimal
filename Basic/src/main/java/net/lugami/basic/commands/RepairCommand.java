package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand {

    @Command(names={"repair"}, permission="basic.repair", description = "Repair the item you're currently holding")
    public static void repair(Player sender) {
        ItemStack item = sender.getItemInHand();
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "You must be holding an item.");
            return;
        }
        if (!Enchantment.DURABILITY.canEnchantItem(item)) {
            sender.sendMessage(ChatColor.RED + ItemUtils.getName((ItemStack)item) + " cannot be repaired.");
            return;
        }
        if (item.getDurability() == 0) {
            sender.sendMessage(ChatColor.RED + "That " + ChatColor.WHITE + ItemUtils.getName((ItemStack)item) + ChatColor.RED + " already has max durability.");
            return;
        }
        item.setDurability((short)0);
        sender.sendMessage(ChatColor.GOLD + "Your " + ChatColor.WHITE + ItemUtils.getName((ItemStack)item) + ChatColor.GOLD + " has been repaired.");
    }
}

