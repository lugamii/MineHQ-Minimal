package net.lugami.bridge.bukkit.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TPSUtil { private TPSUtil() {
}

    public static double[] getTPS() {
        double[] recentTps;
        if (canGetWithPaper()) {
            recentTps = getPaperRecentTps();
        } else {
            recentTps = getNMSRecentTps();
        }
        return recentTps;
    }

    private static final Class<?> spigotServerClass = Reflection.getClass("org.bukkit.Server$Spigot");
    private static final Method getSpigotMethod = Reflection.makeMethod(Bukkit.class, "spigot");
    private static final Method getTPSMethod = spigotServerClass != null ? Reflection.makeMethod(spigotServerClass, "getTPS") : null;
    private static double[] getPaperRecentTps() {
        if (!canGetWithPaper()) throw new UnsupportedOperationException("Can't get TPSUtil from Paper");
        Object server = Reflection.callMethod(getServerMethod, null); // Call static MinecraftServer.getServer()
        double[] recent = Reflection.getField(recentTpsField, server);
        return recent;
    }

    private static boolean canGetWithPaper() {
        return getSpigotMethod != null && getTPSMethod != null;
    }

    private static final Class<?> minecraftServerClass = Reflection.getNmsClass("MinecraftServer");
    private static final Method getServerMethod = minecraftServerClass != null ? Reflection.makeMethod(minecraftServerClass, "getServer") : null;
    private static final Field recentTpsField = minecraftServerClass != null ? Reflection.makeField(minecraftServerClass, "recentTps") : null;
    private static double[] getNMSRecentTps() {
        if (getServerMethod == null || recentTpsField == null) return null;
        Object server = Reflection.callMethod(getServerMethod, null); // Call static MinecraftServer.getServer()
        double[] recent = Reflection.getField(recentTpsField, server);
        return recent;
    }
}