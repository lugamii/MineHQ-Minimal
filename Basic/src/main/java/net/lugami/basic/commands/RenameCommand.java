package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand {

    public static String customNameStarter = ChatColor.translateAlternateColorCodes('&', "&b&c&f");

    @Command(names={"rename"}, permission="basic.rename", description = "Rename the item you're currently holding. Supports color codes")
    public static void rename(Player sender, @Param(name="name", wildcard=true) String name) {
        ItemStack item = sender.getItemInHand();
        if (sender.hasPermission("basic.rename.color")) {
            name = ChatColor.translateAlternateColorCodes('&', name);
        }
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "You must be holding an item.");
            return;
        }
        boolean isCustomEnchant = item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(customNameStarter);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(isCustomEnchant && !name.startsWith(customNameStarter) ? customNameStarter + name : name);
        item.setItemMeta(meta);
        sender.updateInventory();
        sender.sendMessage(ChatColor.GOLD + "Renamed your " + ChatColor.WHITE + ItemUtils.getName((ItemStack)new ItemStack(item.getType(), item.getAmount(), item.getDurability())) + ChatColor.GOLD + " to " + ChatColor.WHITE + name + ChatColor.GOLD + ".");
    }
}

