package net.lugami.practice.kit.menu.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.kit.KitHandler;
import net.lugami.practice.kittype.KitType;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class KitDeleteButton extends Button {

    private final KitType kitType;
    private final int slot;

    KitDeleteButton(KitType kitType, int slot) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Delete";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED + "Click here to delete this kit",
            ChatColor.RED + "You will " + ChatColor.BOLD + "NOT" + ChatColor.RED + " be able to",
            ChatColor.RED + "recover this kit."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        KitHandler kitHandler = Practice.getInstance().getKitHandler();
        kitHandler.removeKit(player, kitType, this.slot);
    }

}