package net.lugami.qlib.boss;

import com.google.common.base.Preconditions;
import net.lugami.qlib.util.EntityUtils;
import net.lugami.qlib.qLib;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.beans.ConstructorProperties;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FrozenBossBarHandler {
    private static boolean initiated = false;
    private static final Map<UUID, BarData> displaying = new HashMap<>();
    private static final Map<UUID, Integer> lastUpdatedPosition = new HashMap<>();
    private static Field spawnPacketAField = null;
    private static Field spawnPacketBField = null;
    private static Field spawnPacketCField = null;
    private static Field spawnPacketDField = null;
    private static Field spawnPacketEField = null;
    private static Field spawnPacketLField = null;
    private static Field metadataPacketAField = null;
    private static Field metadataPacketBField = null;
    private static TObjectIntHashMap classToIdMap = null;

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        Bukkit.getScheduler().runTaskTimer(qLib.getInstance(), () -> {
            for (UUID uuid : displaying.keySet()) {
                int updateTicks;
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return;
                }
                int n = updateTicks = ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() != 47 ? 60 : 3;
                if (lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    return;
                }
                FrozenBossBarHandler.updatePosition(player);
                lastUpdatedPosition.put(player.getUniqueId(), MinecraftServer.currentTick);
            }
        }, 1L, 1L);
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                FrozenBossBarHandler.removeBossBar(event.getPlayer());
            }

            @EventHandler
            public void onPlayerTeleport(PlayerTeleportEvent event) {
                Player player = event.getPlayer();
                if (!displaying.containsKey(player.getUniqueId())) {
                    return;
                }
                BarData data = displaying.get(player.getUniqueId());
                String message = data.message;
                float health = data.health;
                FrozenBossBarHandler.removeBossBar(player);
                FrozenBossBarHandler.setBossBar(player, message, health);
            }
        }, qLib.getInstance());
    }

    public static void setBossBar(Player player, String message, float health) {
        try {
            if (message == null) {
                FrozenBossBarHandler.removeBossBar(player);
                return;
            }
            Preconditions.checkArgument(health >= 0.0f && health <= 1.0f, "Health must be between 0 and 1");
            if (message.length() > 64) {
                message = message.substring(0, 64);
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
            if (!displaying.containsKey(player.getUniqueId())) {
                FrozenBossBarHandler.sendSpawnPacket(player, message, health);
            } else {
                FrozenBossBarHandler.sendUpdatePacket(player, message, health);
            }
            displaying.get(player.getUniqueId()).message = message;
            displaying.get(player.getUniqueId()).health = health;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeBossBar(Player player) {
        if (!displaying.containsKey(player.getUniqueId())) {
            return;
        }
        int entityId = displaying.get(player.getUniqueId()).entityId;
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityId));
        displaying.remove(player.getUniqueId());
        lastUpdatedPosition.remove(player.getUniqueId());
    }

    private static void sendSpawnPacket(Player bukkitPlayer, String message, float health) throws Exception {
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        displaying.put(bukkitPlayer.getUniqueId(), new BarData(EntityUtils.getFakeEntityId(), message, health));
        BarData stored = displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        spawnPacketAField.set(packet, stored.entityId);
        DataWatcher watcher = new DataWatcher((DataWatcher) null);
        if (version != 47) {
            spawnPacketBField.set(packet, (byte)EntityType.ENDER_DRAGON.getTypeId());
            watcher.a(6, Float.valueOf(health * 200.0f));
            spawnPacketCField.set(packet, (int)(player.locX * 32.0));
            spawnPacketDField.set(packet, -6400);
            spawnPacketEField.set(packet, (int)(player.locZ * 32.0));
        } else {
            spawnPacketBField.set(packet, (byte)EntityType.WITHER.getTypeId());
            watcher.a(6, Float.valueOf(health * 300.0f));
            watcher.a(20, 880);
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            spawnPacketCField.set(packet, (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
            spawnPacketDField.set(packet, (int)((player.locY - Math.sin(pitch) * 32.0) * 32.0));
            spawnPacketEField.set(packet, (int)((player.locZ + Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
        }
        watcher.a(version != 47 ? 10 : 2, message);
        spawnPacketLField.set(packet, watcher);
        player.playerConnection.sendPacket(packet);
    }

    private static void sendUpdatePacket(Player bukkitPlayer, String message, float health) throws IllegalAccessException {
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        BarData stored = displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        metadataPacketAField.set(packet, stored.entityId);
        ArrayList<WatchableObject> objects = new ArrayList<WatchableObject>();
        if (health != stored.health) {
            if (version != 47) {
                objects.add(FrozenBossBarHandler.createWatchableObject(6, Float.valueOf(health * 200.0f)));
            } else {
                objects.add(FrozenBossBarHandler.createWatchableObject(6, Float.valueOf(health * 300.0f)));
            }
        }
        if (!message.equals(stored.message)) {
            objects.add(FrozenBossBarHandler.createWatchableObject(version != 47 ? 10 : 2, message));
        }
        metadataPacketBField.set(packet, objects);
        player.playerConnection.sendPacket(packet);
    }

    private static WatchableObject createWatchableObject(int id, Object object) {
        return new WatchableObject(classToIdMap.get(object.getClass()), id, object);
    }

    private static void updatePosition(Player bukkitPlayer) {
        int x;
        int y;
        int z;
        if (!displaying.containsKey(bukkitPlayer.getUniqueId())) {
            return;
        }
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        if (version != 47) {
            x = (int)(player.locX * 32.0);
            y = -6400;
            z = (int)(player.locZ * 32.0);
        } else {
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            x = (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0);
            y = (int)((player.locY - Math.sin(pitch) * 32.0) * 32.0);
            z = (int)((player.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0) * 32.0);
        }
        player.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(FrozenBossBarHandler.displaying.get(bukkitPlayer.getUniqueId()).entityId, x, y, z, (byte)0, (byte)0));
    }

    static {
        try {
            spawnPacketAField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
            spawnPacketAField.setAccessible(true);
            spawnPacketBField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("b");
            spawnPacketBField.setAccessible(true);
            spawnPacketCField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c");
            spawnPacketCField.setAccessible(true);
            spawnPacketDField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("d");
            spawnPacketDField.setAccessible(true);
            spawnPacketEField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("e");
            spawnPacketEField.setAccessible(true);
            spawnPacketLField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            spawnPacketLField.setAccessible(true);
            metadataPacketAField = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            metadataPacketAField.setAccessible(true);
            metadataPacketBField = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            metadataPacketBField.setAccessible(true);
            Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");
            dataWatcherClassToIdField.setAccessible(true);
            classToIdMap = (TObjectIntHashMap)dataWatcherClassToIdField.get(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class BarData {
        private final int entityId;
        private String message;
        private float health;

        @ConstructorProperties(value={"entityId", "message", "health"})
        public BarData(int entityId, String message, float health) {
            this.entityId = entityId;
            this.message = message;
            this.health = health;
        }
    }

}

