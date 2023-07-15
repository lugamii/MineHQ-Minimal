package net.lugami.practice.postmatchinv;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.practice.kittype.HealingMethod;
import net.lugami.practice.kittype.KitType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public final class PostMatchPlayer {

    @Getter private final UUID playerUuid;
    @Getter private final String lastUsername;
    @Getter private final ItemStack[] armor;
    @Getter private final ItemStack[] inventory;
    @Getter private final List<PotionEffect> potionEffects;
    @Getter private final int hunger;
    @Getter private final int health; // out of 10
    @Getter private final transient HealingMethod healingMethodUsed;
    @Getter private final int totalHits;
    @Getter private final int longestCombo;
    @Getter private final int missedPots;
    @Getter private final int ping;
    @Getter private int thrownPots;
    @Getter private int missedDebuffs;
    @Getter private int thrownDebuffs;
    @Getter private KitType kit;

    public PostMatchPlayer(Player player, KitType kit, HealingMethod healingMethodUsed, int totalHits, int longestCombo, int missedPots, int thrownPots, int missedDebuffs, int thrownDebuffs) {
        this.playerUuid = player.getUniqueId();
        this.lastUsername = player.getName();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = ImmutableList.copyOf(player.getActivePotionEffects());
        this.hunger = player.getFoodLevel();
        this.health = (int) player.getHealth();
        this.healingMethodUsed = healingMethodUsed;
        this.totalHits = totalHits;
        this.longestCombo = longestCombo;
        this.missedPots = missedPots;
        this.ping = PlayerUtils.getPing(player);
        this.thrownPots = thrownPots;
        this.missedDebuffs = missedDebuffs;
        this.thrownDebuffs = thrownDebuffs;
        this.kit = kit;
    }

}