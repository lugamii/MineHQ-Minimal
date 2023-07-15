package net.lugami.qlib.configuration.serializers;

import net.lugami.qlib.configuration.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer extends AbstractSerializer<Location> {

    @Override
    public String toString(Location data) {
        return data.getWorld().getName() + "|" + data.getBlockX() + "|" + data.getBlockY() + "|" + data.getBlockZ();
    }

    @Override
    public Location fromString(String data) {
        String[] parts = data.split("\\|");
        return new Location(Bukkit.getWorld(parts[0]), Integer.valueOf(parts[1]).intValue(), Integer.valueOf(parts[2]).intValue(), Integer.valueOf(parts[3]).intValue());
    }
}

