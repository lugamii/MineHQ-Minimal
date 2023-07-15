package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CopyInventoryCommands {

    @Command(names={"cpinv", "cpfrom"}, permission="basic.cpinv", description = "Apply a player's inventory to yours")
    public static void cpinv(Player sender, @Param(name="player") Player player) {
        sender.getInventory().setContents(player.getInventory().getContents());
        sender.getInventory().setArmorContents(player.getInventory().getArmorContents());
        sender.setHealth(player.getHealth());
        sender.setFoodLevel(player.getFoodLevel());
        player.getActivePotionEffects().forEach(((Player)sender)::addPotionEffect);
        sender.sendMessage(player.getDisplayName() + ChatColor.GOLD + "'s inventory has been applied to you.");
    }

    @Command(names={"cpto"}, permission="basic.cpto", description = "Apply your inventory to another player")
    public static void cpto(Player sender, @Param(name="player") Player player) {
        player.getInventory().setContents(sender.getInventory().getContents());
        player.getInventory().setArmorContents(sender.getInventory().getArmorContents());
        player.setHealth(sender.getHealth());
        player.setFoodLevel(sender.getFoodLevel());
        sender.getActivePotionEffects().forEach(((Player)player)::addPotionEffect);
        sender.sendMessage(ChatColor.GOLD + "Your inventory has been applied to " + ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + ".");
    }
}

