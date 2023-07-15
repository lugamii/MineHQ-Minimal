package net.lugami.qlib.combatlogger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.lugami.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatLogger {

    public static final String COMBAT_LOGGER_METADATA = "qLib-CombatLogger";
    private final String playerName;
    private final UUID playerUuid;
    private final ItemStack[] armor;
    private final ItemStack[] inventory;
    private double health = 20.0;
    private final Set<PotionEffect> effects = new HashSet<>();
    private long despawnTime;
    private EntityType entityType = EntityType.VILLAGER;
    private String nameFormat = ChatColor.YELLOW + "%s";
    private CombatLoggerAdapter eventAdapter;
    private LivingEntity spawnedEntity;

    public CombatLogger(Player player, long time, TimeUnit unit) {
        if (!FrozenCombatLoggerHandler.isInitiated()) {
            throw new IllegalArgumentException("FrozenCombatLoggerHandler has not been initiated!");
        }
        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.despawnTime = unit.toSeconds(time);
    }

    public CombatLogger setDespawnTime(long time, TimeUnit unit) {
        this.despawnTime = unit.toSeconds(time);
        return this;
    }

    public CombatLogger setEntityType(EntityType entityType) {
        if (!entityType.isAlive() && !entityType.isSpawnable()) {
            throw new IllegalArgumentException("EntityType must be living and spawnable!");
        }
        this.entityType = entityType;
        return this;
    }

    public CombatLogger setHealth(double health) {
        this.health = health;
        return this;
    }

    public CombatLogger setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public CombatLogger setPotionEffects(Collection<PotionEffect> effects) {
        this.effects.addAll(effects);
        return this;
    }

    public CombatLogger setAdapter(CombatLoggerAdapter adapter) {
        this.eventAdapter = adapter;
        return this;
    }

    public LivingEntity spawn(Location location) {
        final LivingEntity entity = (LivingEntity)location.getWorld().spawnEntity(location, this.entityType);
        entity.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(qLib.getInstance(), "001100010010011110100001"));
        FrozenCombatLoggerHandler.getCombatLoggerMap().put(entity.getUniqueId(), this);
        FrozenCombatLoggerHandler.getCombatLoggerMap().put(this.playerUuid, this);
        entity.setCustomName(String.format(this.nameFormat, this.playerName));
        entity.setCustomNameVisible(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffects(this.effects);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100), true);
        entity.setMaxHealth(this.health + 2.0);
        entity.setHealth(this.health);
        new BukkitRunnable(){

            public void run() {
                if (!entity.isDead() && entity.isValid()) {
                    entity.remove();
                    FrozenCombatLoggerHandler.getCombatLoggerMap().remove(entity.getUniqueId());
                    FrozenCombatLoggerHandler.getCombatLoggerMap().remove(CombatLogger.this.playerUuid);
                }
            }
        }.runTaskLater(qLib.getInstance(), this.despawnTime * 20L);
        this.spawnedEntity = entity;
        return entity;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public double getHealth() {
        return this.health;
    }

    public Set<PotionEffect> getEffects() {
        return this.effects;
    }

    public long getDespawnTime() {
        return this.despawnTime;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getNameFormat() {
        return this.nameFormat;
    }

    public CombatLoggerAdapter getEventAdapter() {
        return this.eventAdapter;
    }

    public LivingEntity getSpawnedEntity() {
        return this.spawnedEntity;
    }

}

