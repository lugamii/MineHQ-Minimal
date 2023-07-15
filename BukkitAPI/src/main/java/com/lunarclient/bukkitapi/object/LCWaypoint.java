package com.lunarclient.bukkitapi.object;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;

@EqualsAndHashCode
@AllArgsConstructor
public final class LCWaypoint {

    @Getter private final String name;
    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int z;
    @Getter private final String world;
    @Getter private final int color;
    @Getter private final boolean forced;
    @Getter private boolean visible;

    public LCWaypoint(String name, Location location, int color, boolean forced, boolean visible) {
        this(name, location.getBlockX(), location.getBlockY(), location.getBlockZ(), LunarClientAPI.getInstance().getWorldIdentifier(location.getWorld()), color, forced, visible);
    }

    public LCWaypoint(String name, Location location, int color, boolean forced) {
        this(name, location.getBlockX(), location.getBlockY(), location.getBlockZ(), LunarClientAPI.getInstance().getWorldIdentifier(location.getWorld()), color, forced, true);
    }

}