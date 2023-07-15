package net.lugami.practice.queue;

import lombok.experimental.UtilityClass;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static net.lugami.practice.PracticeLang.LEFT_ARROW;
import static net.lugami.practice.PracticeLang.RIGHT_ARROW;
import static org.bukkit.ChatColor.*;

@UtilityClass
public final class QueueItems {

    public static final ItemStack JOIN_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_SOLO_UNRANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_SOLO_RANKED_QUEUE_ITEM = new ItemStack(Material.INK_SACK, 1, (byte) DyeColor.RED.getDyeData());

    public static final ItemStack JOIN_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack LEAVE_PARTY_UNRANKED_QUEUE_ITEM = new ItemStack(Material.ARROW);

    public static final ItemStack JOIN_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack LEAVE_PARTY_RANKED_QUEUE_ITEM = new ItemStack(Material.ARROW);

    static {
        ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM, LEFT_ARROW + GREEN + BOLD + "Join " + GRAY + BOLD + "Unranked" + GREEN + BOLD + " Queue" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_SOLO_UNRANKED_QUEUE_ITEM, LEFT_ARROW + RED + BOLD + "Leave Unranked Queue" + RIGHT_ARROW);

        ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, LEFT_ARROW + GREEN + BOLD + "Join " + AQUA + BOLD + "Ranked" + GREEN + BOLD + " Queue" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, LEFT_ARROW + RED + BOLD + "Leave Ranked Queue" + RIGHT_ARROW);

        ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, LEFT_ARROW + GREEN + BOLD + "Play 2v2 Unranked" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, LEFT_ARROW + RED + BOLD + "Leave 2v2 Unranked" + RIGHT_ARROW);

        ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, LEFT_ARROW + GREEN + BOLD + "Play 2v2 Ranked" + RIGHT_ARROW);
        ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, LEFT_ARROW + RED + BOLD + "Leave 2v2 Ranked" + RIGHT_ARROW);

        /*ItemUtils.setDisplayName(JOIN_SOLO_UNRANKED_QUEUE_ITEM, YELLOW + "Play Unranked");
        ItemUtils.setDisplayName(LEAVE_SOLO_UNRANKED_QUEUE_ITEM, RED + "Leave Unranked Queue");

        ItemUtils.setDisplayName(JOIN_SOLO_RANKED_QUEUE_ITEM, GOLD + "Play Ranked");
        ItemUtils.setDisplayName(LEAVE_SOLO_RANKED_QUEUE_ITEM, RED + "Leave Ranked Queue");

        ItemUtils.setDisplayName(JOIN_PARTY_UNRANKED_QUEUE_ITEM, BLUE + "Play 2v2 Unranked");
        ItemUtils.setDisplayName(LEAVE_PARTY_UNRANKED_QUEUE_ITEM, RED + "Leave 2v2 Unranked Queue");

        ItemUtils.setDisplayName(JOIN_PARTY_RANKED_QUEUE_ITEM, DARK_AQUA + "Join 2v2 Ranked");
        ItemUtils.setDisplayName(LEAVE_PARTY_RANKED_QUEUE_ITEM, RED + "Leave 2v2 Ranked Queue");*/
    }

}