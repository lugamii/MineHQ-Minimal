package net.lugami.qlib.combatlogger;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;

public final class FrozenCombatLoggerHandler {

    private static final Map<UUID, CombatLogger> combatLoggerMap = new HashMap<>();
    private static CombatLoggerConfiguration configuration = CombatLoggerConfiguration.DEFAULT_CONFIGURATION;
    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        Bukkit.getPluginManager().registerEvents(new CombatLoggerListener(), qLib.getInstance());
    }

    public static Map<UUID, CombatLogger> getCombatLoggerMap() {
        return combatLoggerMap;
    }

    public static CombatLoggerConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(CombatLoggerConfiguration configuration) {
        FrozenCombatLoggerHandler.configuration = configuration;
    }

    static boolean isInitiated() {
        return initiated;
    }
}

