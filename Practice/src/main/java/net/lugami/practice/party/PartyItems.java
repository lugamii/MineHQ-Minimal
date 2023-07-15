package net.lugami.practice.party;

import lombok.experimental.UtilityClass;
import net.lugami.qlib.util.ItemUtils;
import net.lugami.qlib.util.UUIDUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;
import static net.lugami.practice.PracticeLang.LEFT_ARROW;
import static net.lugami.practice.PracticeLang.RIGHT_ARROW;

@UtilityClass
public final class PartyItems {

    public static final Material ICON_TYPE = Material.NETHER_STAR;

    public static final ItemStack LEAVE_PARTY_ITEM = new ItemStack(Material.FIRE);
    public static final ItemStack ASSIGN_CLASSES = new ItemStack(Material.MAGMA_CREAM);
    public static final ItemStack START_TEAM_SPLIT_ITEM = new ItemStack(Material.DIAMOND_SWORD);
    public static final ItemStack START_FFA_ITEM = new ItemStack(Material.GOLD_SWORD);
    public static final ItemStack OTHER_PARTIES_ITEM = new ItemStack(Material.SKULL_ITEM);

    static {
        ItemUtils.setDisplayName(LEAVE_PARTY_ITEM, LEFT_ARROW + RED + BOLD + "Leave Party" + RIGHT_ARROW);
        ItemUtils.setDisplayName(ASSIGN_CLASSES, LEFT_ARROW + YELLOW + BOLD + "Assign HCF Kits" + RIGHT_ARROW);
        ItemUtils.setDisplayName(START_TEAM_SPLIT_ITEM, LEFT_ARROW + YELLOW + BOLD + "Start Team Split" + RIGHT_ARROW);
        ItemUtils.setDisplayName(START_FFA_ITEM, LEFT_ARROW + YELLOW + BOLD + "Start Party FFA" + RIGHT_ARROW);
        ItemUtils.setDisplayName(OTHER_PARTIES_ITEM, LEFT_ARROW + GREEN + BOLD + "Other Parties" + RIGHT_ARROW);
    }

    public static ItemStack icon(Party party) {
        ItemStack item = new ItemStack(ICON_TYPE);

        String leaderName = UUIDUtils.name(party.getLeader());
        String displayName = LEFT_ARROW + AQUA + BOLD + leaderName + AQUA + "'s Party" + RIGHT_ARROW;

        ItemUtils.setDisplayName(item, displayName);
        return item;
    }

}
