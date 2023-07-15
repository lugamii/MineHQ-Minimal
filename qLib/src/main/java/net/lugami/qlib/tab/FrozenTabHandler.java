package net.lugami.qlib.tab;

import com.google.common.base.Preconditions;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.lugami.qlib.qLib;

import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class FrozenTabHandler {

    private static boolean initiated = false;
    private static final AtomicReference<Object> propertyMapSerializer = new AtomicReference<>();
    private static final AtomicReference<Object> defaultPropertyMap = new AtomicReference<>();
    private static LayoutProvider layoutProvider;
    private static final Map<UUID, FrozenTab> tabs = new ConcurrentHashMap<>();

    public static void init() {
        if (qLib.getInstance().getConfig().getBoolean("disableTab", false)) {
            return;
        }
        Preconditions.checkState((!initiated));
        initiated = true;
        FrozenTabHandler.getDefaultPropertyMap();
        new TabThread().start();
        qLib.getInstance().getServer().getPluginManager().registerEvents(new TabListener(), qLib.getInstance());
    }

    public static void setLayoutProvider(LayoutProvider provider) {
        layoutProvider = provider;
    }

    protected static void addPlayer(Player player) {
        tabs.put(player.getUniqueId(), new FrozenTab(player));
    }

    protected static void updatePlayer(Player player) {
        if (tabs.containsKey(player.getUniqueId())) {
            tabs.get(player.getUniqueId()).update();
        }
    }

    protected static void removePlayer(Player player) {
        tabs.remove(player.getUniqueId());
    }

    private static PropertyMap fetchSkin() {
        String propertyMap = qLib.getInstance().runBackboneRedisCommand(redis -> redis.get("propertyMap"));
        if (propertyMap != null && !propertyMap.isEmpty()) {
            Bukkit.getLogger().info("Using cached PropertyMap for skin...");
            JsonArray jsonObject = new JsonParser().parse(propertyMap).getAsJsonArray();
            return FrozenTabHandler.getPropertyMapSerializer().deserialize(jsonObject, null, null);
        }
        GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad");
        YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
        GameProfile profile1 = sessionService.fillProfileProperties(profile, true);
        final PropertyMap localPropertyMap = profile1.getProperties();
        qLib.getInstance().runBackboneRedisCommand(redis -> {
            Bukkit.getLogger().info("Caching PropertyMap for skin...");
            redis.setex("propertyMap", 3600, FrozenTabHandler.getPropertyMapSerializer().serialize(localPropertyMap, null, null).toString());
            return null;
        });
        return localPropertyMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyMap.Serializer getPropertyMapSerializer() {
        Object value = propertyMapSerializer.get();
        if (value == null) {
            synchronized (propertyMapSerializer) {
                value = propertyMapSerializer.get();
                if (value == null) {
                    PropertyMap.Serializer actualValue = new PropertyMap.Serializer();
                    value = actualValue == null ? propertyMapSerializer : actualValue;
                    propertyMapSerializer.set(value);
                }
            }
        }
        return (PropertyMap.Serializer)(value == propertyMapSerializer ? null : value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyMap getDefaultPropertyMap() {
        Object value = defaultPropertyMap.get();
        if (value == null) {
            synchronized (defaultPropertyMap) {
                value = defaultPropertyMap.get();
                if (value == null) {
                    PropertyMap actualValue = FrozenTabHandler.fetchSkin();
                    value = actualValue == null ? defaultPropertyMap : actualValue;
                    defaultPropertyMap.set(value);
                }
            }
        }
        return (PropertyMap)(value == defaultPropertyMap ? null : value);
    }

    public static LayoutProvider getLayoutProvider() {
        return layoutProvider;
    }

    public static Map<UUID, FrozenTab> getTabs() {
        return tabs;
    }

}
