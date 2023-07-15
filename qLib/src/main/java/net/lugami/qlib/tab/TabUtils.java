package net.lugami.qlib.tab;

import net.lugami.qlib.qLib;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TabUtils {

    private static final Map<String, GameProfile> cache = new ConcurrentHashMap<>();

    public static boolean is18(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 20;
    }

    public static GameProfile getOrCreateProfile(String name, UUID id) {
        GameProfile player = cache.get(name);
        if (player == null) {
            player = new GameProfile(id, name);
            player.getProperties().putAll(FrozenTabHandler.getDefaultPropertyMap());
            cache.put(name, player);
        }
        return player;
    }

    public static GameProfile getOrCreateProfile(String name) {
        return TabUtils.getOrCreateProfile(name, new UUID(qLib.RANDOM.nextLong(), qLib.RANDOM.nextLong()));
    }
}

