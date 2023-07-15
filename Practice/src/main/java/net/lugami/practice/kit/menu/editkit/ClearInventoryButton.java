package net.lugami.practice.kit.menu.editkit;

import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class ClearInventoryButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Clear Inventory";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "This will clear your inventory",
            ChatColor.YELLOW + "so you can start over."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.YELLOW.getWoolData();
    }

    @Override
    public void clicked(final Player player, int slot, ClickType clickType) {
        player.getInventory().clear();

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

}