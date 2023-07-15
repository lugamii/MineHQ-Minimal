package net.lugami.practice.rematch;

import lombok.experimental.UtilityClass;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.BOLD;

@UtilityClass
public final class RematchItems {

    public static final ItemStack REQUEST_REMATCH_ITEM = new ItemStack(Material.DIAMOND);
    public static final ItemStack SENT_REMATCH_ITEM = new ItemStack(Material.EMERALD);
    public static final ItemStack ACCEPT_REMATCH_ITEM = new ItemStack(Material.EMERALD);

    static {
        ItemUtils.setDisplayName(REQUEST_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + DARK_PURPLE + BOLD + "Request Rematch" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(SENT_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Sent Rematch" + BLUE.toString() + BOLD + " «");
        ItemUtils.setDisplayName(ACCEPT_REMATCH_ITEM, BLUE.toString() + BOLD + "» " + GREEN + BOLD + "Accept Rematch" + BLUE.toString() + BOLD + " «");
    }

}