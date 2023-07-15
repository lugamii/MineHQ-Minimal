package net.lugami.practice.util;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.cheatbreaker.api.object.CBCooldown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public enum CheatBreakerKey {

    ENDER_PEARL("Enderpearl", Material.ENDER_PEARL),
    FIRE_BALL("Fireball", Material.FIREBALL),
    BARD_EFFECT("Bard_", Material.GOLD_HELMET);

    private String name;
    private Material icon;

    public void send(Player player, Long duration) {
        if (Bukkit.getPluginManager().getPlugin("CheatBreakerAPI") == null || !Bukkit.getPluginManager().getPlugin("CheatBreakerAPI").isEnabled()) return;
        if (player == null || duration == null || duration < 0)
            return;

        CheatBreakerAPI.getInstance().sendCooldown(player, new CBCooldown(name, duration, TimeUnit.MILLISECONDS, icon));
    }

    public void sendBard(Player player, PotionEffect potionEffect, Material material, Long duration) {
        if (Bukkit.getPluginManager().getPlugin("CheatBreakerAPI") == null || !Bukkit.getPluginManager().getPlugin("CheatBreakerAPI").isEnabled()) return;
        if (player == null || duration == null || duration < 0)
            return;

        String name = getName() + getFriendlyEffectName(potionEffect.getType());

        CheatBreakerAPI.getInstance().sendCooldown(player, new CBCooldown(name, duration, TimeUnit.MILLISECONDS, material));
    }

    public void clear(Player player) {
        send(player, 0L);
    }

    private String getFriendlyEffectName(PotionEffectType potionEffectType) {
        if (potionEffectType == PotionEffectType.INCREASE_DAMAGE) {
            return "Strength";
        } else if (potionEffectType == PotionEffectType.DAMAGE_RESISTANCE) {
            return "Resistance";
        } else if (potionEffectType == PotionEffectType.REGENERATION) {
            return "Regen";
        } else {
            return WordUtils.capitalize(potionEffectType.getName().toLowerCase().replaceAll("_", ""));
        }
    }
}
