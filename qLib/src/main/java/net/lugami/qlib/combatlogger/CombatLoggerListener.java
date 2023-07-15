package net.lugami.qlib.combatlogger;

import java.util.UUID;

import net.lugami.qlib.deathmessage.damage.Damage;
import net.lugami.qlib.deathmessage.DeathMessageConfiguration;
import net.lugami.qlib.deathmessage.FrozenDeathMessageHandler;
import net.lugami.qlib.qLib;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatLoggerListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        CombatLogger logger;
        if (event.getEntity().hasMetadata("qLib-CombatLogger") && (logger = FrozenCombatLoggerHandler.getCombatLoggerMap().get(event.getEntity().getUniqueId())) != null) {
            Player target;
            EntityPlayer entity;
            MinecraftServer server;
            for (ItemStack item : logger.getArmor()) {
                event.getDrops().add(item);
            }
            for (ItemStack item : logger.getInventory()) {
                event.getDrops().add(item);
            }
            logger.getEventAdapter().onEntityDeath(logger, event);
            CombatLoggerConfiguration configuration = FrozenCombatLoggerHandler.getConfiguration();
            final DeathMessageConfiguration dmConfig = FrozenDeathMessageHandler.getConfiguration();
            final Player killer = event.getEntity().getKiller();
            if (configuration != null && dmConfig != null) {
                new BukkitRunnable(){

                    public void run() {
                        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
                            String deathMessage = CombatLoggerListener.this.getCombatLoggerDeathMessage(logger.getPlayerUuid(), killer, player.getUniqueId());
                            boolean showDeathMessage = dmConfig.shouldShowDeathMessage(player.getUniqueId(), logger.getPlayerUuid(), killer == null ? null : killer.getUniqueId());
                            if (!showDeathMessage) continue;
                            player.sendMessage(deathMessage);
                        }
                    }
                }.runTaskAsynchronously((Plugin) qLib.getInstance());
            }
            if ((target = qLib.getInstance().getServer().getPlayer(logger.getPlayerUuid())) == null && (target = (entity = new EntityPlayer(server = ((CraftServer) qLib.getInstance().getServer()).getServer(), server.getWorldServer(0), new GameProfile(logger.getPlayerUuid(), logger.getPlayerName()), new PlayerInteractManager((World)server.getWorldServer(0)))).getBukkitEntity()) != null) {
                target.loadData();
            }
            if (target != null) {
                target.getInventory().clear();
                target.getInventory().setArmorContents(null);
                target.saveData();
            }
            FrozenCombatLoggerHandler.getCombatLoggerMap().remove(event.getEntity().getUniqueId());
            FrozenCombatLoggerHandler.getCombatLoggerMap().remove(logger.getPlayerUuid());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata("qLib-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!entity.hasMetadata("qLib-CombatLogger") || entity.isDead()) continue;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata("qLib-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        CombatLogger logger = FrozenCombatLoggerHandler.getCombatLoggerMap().get(event.getPlayer().getUniqueId());
        if (logger != null && logger.getSpawnedEntity() != null && logger.getSpawnedEntity().isValid() && !logger.getSpawnedEntity().isDead()) {
            UUID entityId = logger.getSpawnedEntity().getUniqueId();
            logger.getSpawnedEntity().remove();
            FrozenCombatLoggerHandler.getCombatLoggerMap().remove(entityId);
            FrozenCombatLoggerHandler.getCombatLoggerMap().remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata("qLib-CombatLogger")) {
            return;
        }
        CombatLogger logger = FrozenCombatLoggerHandler.getCombatLoggerMap().get(event.getEntity().getUniqueId());
        if (logger != null) {
            logger.getEventAdapter().onEntityDamageByEntity(logger, event);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onEntityPressurePlate(EntityInteractEvent event) {
        boolean pressurePlate;
        boolean bl = pressurePlate = event.getBlock().getType() == Material.STONE_PLATE || event.getBlock().getType() == Material.GOLD_PLATE || event.getBlock().getType() == Material.IRON_PLATE || event.getBlock().getType() == Material.WOOD_PLATE;
        if (pressurePlate && event.getEntity().hasMetadata("qLib-CombatLogger")) {
            event.setCancelled(true);
        }
    }

    private String getCombatLoggerDeathMessage(UUID player, Player killer, UUID getFor) {
        if (killer == null) {
            return this.wrapLogger(player, getFor) + (Object)ChatColor.YELLOW + " died.";
        }
        ItemStack hand = killer.getItemInHand();
        String itemString = hand.getType() == Material.AIR ? "their fists" : (hand.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)hand.getItemMeta().getDisplayName()) : WordUtils.capitalizeFully((String)hand.getType().name().replace('_', ' ')));
        return this.wrapLogger(player, getFor) + (Object)ChatColor.YELLOW + " was slain by " + Damage.wrapName(killer.getUniqueId(), getFor) + ChatColor.YELLOW + " using " + (Object)ChatColor.RED + itemString.trim() + (Object)ChatColor.YELLOW + ".";
    }

    private String wrapLogger(UUID player, UUID wrapFor) {
        CombatLoggerConfiguration configuration = FrozenCombatLoggerHandler.getConfiguration();
        return configuration.formatPlayerName(player, wrapFor);
    }

}

