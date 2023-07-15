package net.lugami.basic.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportationListener implements Listener {

    private static Map<UUID, Location> lastLocation = new HashMap<>();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause.name().contains("PEARL") || cause.name().contains("PORTAL")) {
            return;
        }
        if (player.hasPermission("basic.teleport")) {
            lastLocation.put(player.getUniqueId(), event.getFrom());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("basic.teleport")) {
            lastLocation.put(player.getUniqueId(), player.getLocation());
        }
    }

    public static Map<UUID, Location> getLastLocation() {
        return lastLocation;
    }
}

