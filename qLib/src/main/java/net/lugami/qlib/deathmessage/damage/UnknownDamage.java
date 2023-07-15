package net.lugami.qlib.deathmessage.damage;

import java.util.UUID;

import org.bukkit.ChatColor;

public final class UnknownDamage extends Damage {

    public UnknownDamage(UUID damaged, double damage) {
        super(damaged, damage);
    }

    @Override
    public String getDeathMessage(UUID getFor) {
        return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " died.";
    }
}

