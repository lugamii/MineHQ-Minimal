package org.bukkit.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class GuardianEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private final Cheat cheat;
    @Getter private final String module;

    @Getter private final DisplayLevel level;

    @Getter private final String message;
    @Getter private final Location location;

    @Getter private Map<String, Object> data = new HashMap<String, Object>();

    public GuardianEvent(Player player, Cheat cheat, String module, DisplayLevel level, String message) {
        super(player);

        this.cheat = cheat;
        this.level = level;
        this.module = module;
        this.message = message;
        this.location = player.getLocation();
    }

    // Use this constructor if you want to override the location of the alert
    public GuardianEvent(Player player, Cheat cheat, String module, DisplayLevel level, String message, Location location) {
        super(player);

        this.cheat = cheat;
        this.level = level;
        this.module = module;
        this.message = message;
        this.location = location;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public GuardianEvent addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public int getInt(String key) {
        return (Integer) data.get(key);
    }

    public String getString(String key) {
        return (String) data.get(key);
    }

    public double getDouble(String key) {
        return (Double) data.get(key);
    }

    public enum Cheat {

        SPEED_HACKS,
        FLY_HACKS,
        AUTO_CLICKER,
        KILL_AURA,
        HOVER,
        CRITICALS,
        NO_FALL,
        TIMER,
        PHASE,
        FAST_USE,
        REGENERATION,
        CLIENT_MODIFICATIONS,
        REACH,

        GENERAL, // Used for wrongly formed packets and other stuff

        DEBUG // Used for debugging only

    }

    public enum DisplayLevel {

        LOW,
        MEDIUM,
        HIGH,
        HIGHEST;

        public boolean willDisplay(Player player) {
            return player.hasPermission("guardian.display." + name().toLowerCase());
        }

    }

}