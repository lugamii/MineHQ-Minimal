package net.lugami.practice.match;

import net.lugami.qlib.util.ItemUtils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SpectatorItems {

    public static final ItemStack SHOW_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.GRAY.getDyeData());
    public static final ItemStack HIDE_SPECTATORS_ITEM = new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData());
    public static final ItemStack TP_TO_LAST_PVP = new ItemStack(Material.EMERALD);
    public static final ItemStack VIEW_INVENTORY_ITEM = new ItemStack(Material.BOOK);

    public static final ItemStack RETURN_TO_LOBBY_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.FIRE);

    static {
        ItemUtils.setDisplayName(SHOW_SPECTATORS_ITEM, ChatColor.YELLOW + "Show spectators");
        ItemUtils.setDisplayName(HIDE_SPECTATORS_ITEM, ChatColor.YELLOW + "Hide spectators");
        ItemUtils.setDisplayName(VIEW_INVENTORY_ITEM, ChatColor.YELLOW + "View player inventory");

        ItemUtils.setDisplayName(TP_TO_LAST_PVP, ChatColor.YELLOW + "Teleport to Last PvP");

        ItemUtils.setDisplayName(RETURN_TO_LOBBY_ITEM, ChatColor.YELLOW + "Return to player");
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, ChatColor.YELLOW + "Leave party");
    }

}