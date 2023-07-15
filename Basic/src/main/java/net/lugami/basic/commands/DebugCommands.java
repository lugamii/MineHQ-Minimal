package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DebugCommands {

    @Command(names={"itemdebug"}, permission="op")
    public static void debug(Player sender) {
        sender.sendMessage(qLib.GSON.toJson(sender.getItemInHand()).replace('§', '&'));
    }

    @Command(names={"loadkit"}, permission="op")
    public static void loadkit(Player player) {
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
        player.getInventory().addItem(new ItemStack(Material.POTION, 64, (short) 8226));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1));
        player.sendMessage(ChatColor.GREEN + "Loaded test kit.");
    }
}

