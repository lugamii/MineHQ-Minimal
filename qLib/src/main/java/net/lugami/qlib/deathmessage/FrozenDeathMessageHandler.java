package net.lugami.qlib.deathmessage;

import com.google.common.base.Preconditions;
import net.lugami.qlib.deathmessage.damage.Damage;
import net.lugami.qlib.deathmessage.listener.DamageListener;
import net.lugami.qlib.deathmessage.listener.DeathListener;
import net.lugami.qlib.deathmessage.listener.DisconnectListener;
import net.lugami.qlib.deathmessage.tracker.*;
import net.lugami.qlib.qLib;
import lombok.NoArgsConstructor;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import net.lugami.qlib.deathmessage.tracker.*;

import java.util.*;

@NoArgsConstructor
public final class FrozenDeathMessageHandler {

    private static DeathMessageConfiguration configuration = DeathMessageConfiguration.DEFAULT_CONFIGURATION;
    private static final Map<UUID, List<Damage>> damage = new HashMap<>();
    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        PluginManager pluginManager = qLib.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(new DamageListener(), qLib.getInstance());
        pluginManager.registerEvents(new DeathListener(), qLib.getInstance());
        pluginManager.registerEvents(new DisconnectListener(), qLib.getInstance());
        pluginManager.registerEvents(new GeneralTracker(), qLib.getInstance());
        pluginManager.registerEvents(new PvPTracker(), qLib.getInstance());
        pluginManager.registerEvents(new EntityTracker(), qLib.getInstance());
        pluginManager.registerEvents(new FallTracker(), qLib.getInstance());
        pluginManager.registerEvents(new ArrowTracker(), qLib.getInstance());
        pluginManager.registerEvents(new VoidTracker(), qLib.getInstance());
        pluginManager.registerEvents(new BurnTracker(), qLib.getInstance());
    }

    public static List<Damage> getDamage(Player player) {
        return damage.containsKey(player.getUniqueId()) ? damage.get(player.getUniqueId()) : ImmutableList.of();
    }

    public static void addDamage(Player player, Damage addedDamage) {
        damage.putIfAbsent(player.getUniqueId(), new ArrayList<>());
        List<Damage> damageList = damage.get(player.getUniqueId());
        while (damageList.size() > 30) {
            damageList.remove(0);
        }
        damageList.add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getUniqueId());
    }

    public static DeathMessageConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(DeathMessageConfiguration configuration) {
        FrozenDeathMessageHandler.configuration = configuration;
    }
}

