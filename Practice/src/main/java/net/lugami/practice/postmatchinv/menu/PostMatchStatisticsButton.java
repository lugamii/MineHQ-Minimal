package net.lugami.practice.postmatchinv.menu;

import com.google.common.collect.ImmutableList;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.kittype.HealingMethod;
import net.lugami.practice.kittype.KitType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

final class PostMatchStatisticsButton extends Button {

    private final KitType kitType;
    private final HealingMethod healingMethodUsed;
    private final int totalHits;
    private final int longestCombo;
    private final double missedHeals;
    private final double thrownHeals;
    private final double missedDebuffs;
    private final double thrownDebuffs;

    PostMatchStatisticsButton(KitType kitType, HealingMethod healingMethodUsed, int totalHits, int longestCombo, double missedHeals, double thrownHeals, double missedDebuffs, double thrownDebuffs) {
        this.kitType = kitType;
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedHeals = missedHeals;
        this.thrownHeals = thrownHeals;
        this.missedDebuffs = missedDebuffs;
        this.thrownDebuffs = thrownDebuffs;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Match Stats";
    }

    @Override
    public List<String> getDescription(Player player) {
        if (healingMethodUsed != HealingMethod.POTIONS) {
            return ImmutableList.of(
                    ChatColor.LIGHT_PURPLE + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                    ChatColor.LIGHT_PURPLE + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""));
        }
        if (kitType.getId().equals("Debuff") || kitType.getId().equals("Vanilla")) {
            return ImmutableList.of(
                    ChatColor.LIGHT_PURPLE + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                    ChatColor.LIGHT_PURPLE + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""),
                    ChatColor.LIGHT_PURPLE + "Potion Accuracy: " + ChatColor.YELLOW + (getPotionAccuracy() == -1 ? "N/A" : getPotionAccuracy() + "%"),
                    ChatColor.LIGHT_PURPLE + "Debuff Accuracy: " + ChatColor.YELLOW + (getDebuffAccuracy() == -1 ? "N/A" : getDebuffAccuracy() + "%"));
        }
        return ImmutableList.of(
                ChatColor.LIGHT_PURPLE + "Hits: " + ChatColor.YELLOW + totalHits + "" + (totalHits == 1 ? "" : ""),
                ChatColor.LIGHT_PURPLE + "Longest Combo: " + ChatColor.YELLOW + longestCombo + "" + (longestCombo == 1 ? "" : ""),
                ChatColor.LIGHT_PURPLE + "Potion Accuracy: " + ChatColor.YELLOW + (getPotionAccuracy() == -1 ? "N/A" : getPotionAccuracy() + "%"));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

    public int getPotionAccuracy() {
        if (thrownHeals == 0) {
            return -1;
        } else if (missedHeals == 0) {
            return 100;
        } else if (thrownHeals == missedHeals) {
            return 50;
        }

        return (int) Math.round(100 - ((missedHeals / thrownHeals) * 100));
    }

    public int getDebuffAccuracy() {
        if (thrownDebuffs == 0) {
            return -1;
        } else if (missedDebuffs == 0) {
            return 100;
        } else if (thrownDebuffs == missedDebuffs) {
            return 50;
        }

        return (int) Math.round(100 - ((missedDebuffs / thrownDebuffs) * 100));
    }

}