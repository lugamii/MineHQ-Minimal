package net.lugami.practice.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.kittype.HealingMethod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

final class PostMatchHealsLeftButton extends Button {

    private final UUID player;
    private final HealingMethod healingMethod;
    private final int healsRemaining;
    private final int missedHeals;

    PostMatchHealsLeftButton(UUID player, HealingMethod healingMethod, int healsRemaining, int missedHeals) {
        this.player = player;
        this.healingMethod = healingMethod;
        this.healsRemaining = healsRemaining;
        this.missedHeals = missedHeals;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + healsRemaining + " " + (healsRemaining == 1 ? healingMethod.getLongSingular() : healingMethod.getLongPlural());
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.YELLOW + UUIDUtils.name(this.player) + " had " + healsRemaining + " " + (healsRemaining == 1 ? healingMethod.getLongSingular() : healingMethod.getLongPlural()) + " left.");
//            ChatColor.YELLOW + UUIDUtils.name(this.player) + " missed " + missedHeals + " health potion" + (missedHeals == 1 ? "." : "s.")
//        );
    }

    @Override
    public Material getMaterial(Player player) {
        return healingMethod.getIconType();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack item = super.getButtonItem(player);
        item.setDurability(healingMethod.getIconDurability());
        return item;
    }

    @Override
    public int getAmount(Player player) {
        return healsRemaining;
    }

}