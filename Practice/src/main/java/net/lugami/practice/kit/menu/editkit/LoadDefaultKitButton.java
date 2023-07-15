package net.lugami.practice.kit.menu.editkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.kittype.KitType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class LoadDefaultKitButton extends Button {

    private final KitType kitType;

    LoadDefaultKitButton(KitType kitType) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Load default kit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click this to load the default kit",
            ChatColor.YELLOW + "into the kit editing menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.GRAY.getWoolData();
    }

    @Override
    public void clicked(final Player player, int slot, ClickType clickType) {
        /* Duplication fix. When players click this button we must set whatever they might have in their hand to air
         * Otherwise they can duplicate items infinitely. This exploits kits like archer and axe pvp. */
        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.getInventory().setContents(kitType.getDefaultInventory());

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), player::updateInventory, 1L);
    }

}