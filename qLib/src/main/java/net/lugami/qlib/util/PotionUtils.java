package net.lugami.qlib.util;

import lombok.NoArgsConstructor;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public final class PotionUtils {

    private static final Map<PotionEffectType, String> displayNames = new HashMap<>();

    public static String getName(PotionEffectType type) {
        return displayNames.get(type);
    }

    public static PotionEffectType parse(String input) {
        for (Map.Entry<PotionEffectType, String> entry : displayNames.entrySet()) {
            if (!entry.getValue().replace(" ", "").equalsIgnoreCase(input)) continue;
            return entry.getKey();
        }
        for (PotionEffectType type : PotionEffectType.values()) {
            if (!input.equalsIgnoreCase(type.toString())) continue;
            return type;
        }
        return null;
    }

    static {
        displayNames.put(PotionEffectType.ABSORPTION, "Absorption");
        displayNames.put(PotionEffectType.BLINDNESS, "Blindness");
        displayNames.put(PotionEffectType.CONFUSION, "Nausea");
        displayNames.put(PotionEffectType.DAMAGE_RESISTANCE, "Damage Resistance");
        displayNames.put(PotionEffectType.FAST_DIGGING, "Haste");
        displayNames.put(PotionEffectType.FIRE_RESISTANCE, "Fire Resistance");
        displayNames.put(PotionEffectType.HARM, "Instant Damage");
        displayNames.put(PotionEffectType.HEAL, "Instant Heal");
        displayNames.put(PotionEffectType.HEALTH_BOOST, "Health Boost");
        displayNames.put(PotionEffectType.HUNGER, "Hunger");
        displayNames.put(PotionEffectType.INCREASE_DAMAGE, "Strength");
        displayNames.put(PotionEffectType.INVISIBILITY, "Invisibility");
        displayNames.put(PotionEffectType.JUMP, "Jump Boost");
        displayNames.put(PotionEffectType.NIGHT_VISION, "Night Vision");
        displayNames.put(PotionEffectType.POISON, "Poison");
        displayNames.put(PotionEffectType.REGENERATION, "Regeneration");
        displayNames.put(PotionEffectType.SATURATION, "Saturation");
        displayNames.put(PotionEffectType.SLOW, "Slowness");
        displayNames.put(PotionEffectType.SLOW_DIGGING, "Mining Fatigue");
        displayNames.put(PotionEffectType.SPEED, "Speed");
        displayNames.put(PotionEffectType.WATER_BREATHING, "Water Breathing");
        displayNames.put(PotionEffectType.WEAKNESS, "Weakness");
        displayNames.put(PotionEffectType.WITHER, "Wither");
    }
}

