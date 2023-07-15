package net.lugami.basic.commands;

import net.lugami.basic.util.EnchantmentWrapper;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Flag;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand {

    @Command(names={"enchant"}, permission="basic.enchant", description = "Enchant an item")
    public static void enchant(Player sender, @Flag(value={"h", "hotbar"}, description = "Enchant your entire hotbar") boolean hotbar, @Param(name="enchantment") Enchantment enchantment, @Param(name="level", defaultValue="1") int level) {
        if (level <= 0) {
            sender.sendMessage(ChatColor.RED + "The level must be greater than 0.");
            return;
        }
        if (!hotbar) {
            ItemStack item = sender.getItemInHand();
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "You must be holding an item.");
                return;
            }
            EnchantmentWrapper wrapper = EnchantmentWrapper.parse(enchantment);
            if (level > wrapper.getMaxLevel()) {
                if (!sender.hasPermission("basic.enchant.force")) {
                    sender.sendMessage(ChatColor.RED + "The maximum enchanting level for " + wrapper.getFriendlyName() + " is " + level + ". You provided " + level + ".");
                    return;
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + wrapper.getFriendlyName() + " " + level + " to this item. The default maximum value is " + wrapper.getMaxLevel() + ".");
            }
            wrapper.enchant(item, level);
            sender.updateInventory();
            sender.sendMessage(ChatColor.GOLD + "Enchanted your " + ChatColor.WHITE + ItemUtils.getName((ItemStack)item) + ChatColor.GOLD + " with " + ChatColor.WHITE + wrapper.getFriendlyName() + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".");
        } else {
            EnchantmentWrapper wrapper2 = EnchantmentWrapper.parse(enchantment);
            if (level > wrapper2.getMaxLevel() && !sender.hasPermission("basic.enchant.force")) {
                sender.sendMessage(ChatColor.RED + "The maximum enchanting level for " + wrapper2.getFriendlyName() + " is " + level + ". You provided " + level + ".");
                return;
            }
            int enchanted = 0;
            for (int slot = 0; slot < 9; ++slot) {
                ItemStack item2 = sender.getInventory().getItem(slot);
                if (item2 == null || !wrapper2.canEnchantItem(item2)) continue;
                wrapper2.enchant(item2, level);
                ++enchanted;
            }
            if (enchanted == 0) {
                sender.sendMessage(ChatColor.RED + "No items in your hotbar can be enchanted with " + wrapper2.getFriendlyName() + ".");
                return;
            }
            if (level > wrapper2.getMaxLevel()) {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING: " + ChatColor.YELLOW + "You added " + wrapper2.getFriendlyName() + " " + level + " to these items. The default maximum value is " + wrapper2.getMaxLevel() + ".");
            }
            sender.sendMessage(ChatColor.GOLD + "Enchanted " + ChatColor.WHITE + enchanted + ChatColor.GOLD + " items with " + ChatColor.WHITE + wrapper2.getFriendlyName() + ChatColor.GOLD + " level " + ChatColor.WHITE + level + ChatColor.GOLD + ".");
            sender.updateInventory();
        }
    }
}

