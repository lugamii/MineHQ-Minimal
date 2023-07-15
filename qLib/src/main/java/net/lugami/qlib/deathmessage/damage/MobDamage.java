package net.lugami.qlib.deathmessage.damage;

import java.util.UUID;

import org.bukkit.entity.EntityType;

public abstract class MobDamage extends Damage {

    private final EntityType mobType;

    public MobDamage(UUID damaged, double damage, EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

    public EntityType getMobType() {
        return this.mobType;
    }
}

