package net.lugami.qlib.border;

import java.util.concurrent.TimeUnit;

import net.lugami.qlib.border.event.PlayerBorderEvent;
import net.lugami.qlib.border.event.PlayerEnterBorderEvent;
import net.lugami.qlib.border.event.PlayerExitBorderEvent;
import net.lugami.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class BorderListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        boolean movedBlock;
        Location fromLoc = event.getFrom();
        Location toLoc = event.getTo();
        Border border = FrozenBorderHandler.getBorderForWorld(fromLoc.getWorld());
        if (border == null) {
            return;
        }
        boolean from = border.contains(fromLoc.getBlockX(), fromLoc.getBlockZ());
        boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());
        boolean bl = movedBlock = event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
        if (movedBlock) {
            PlayerBorderEvent playerBorderEvent = null;
            if (from && !to) {
                playerBorderEvent = new PlayerExitBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
            } else if (!from && to) {
                playerBorderEvent = new PlayerEnterBorderEvent(border, event.getPlayer(), fromLoc, toLoc);
            }
            if (playerBorderEvent != null) {
                Bukkit.getPluginManager().callEvent(playerBorderEvent);
                if (playerBorderEvent.isCancelled()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVehicleUpdate(VehicleUpdateEvent event) {
        if (!(event.getVehicle().getPassenger() instanceof Player)) {
            return;
        }
        Border border = FrozenBorderHandler.getBorderForWorld(event.getVehicle().getWorld());
        if (border == null) {
            return;
        }
        Player player = (Player)event.getVehicle().getPassenger();
        Vehicle vehicle = event.getVehicle();
        if (!border.contains(vehicle.getLocation()) && vehicle instanceof Horse) {
            Location location = vehicle.getLocation();
            Cuboid cuboid = border.getPhysicalBounds();
            double validX = location.getX();
            double validZ = location.getZ();
            if (location.getBlockX() + 2 > cuboid.getUpperX()) {
                validX = cuboid.getUpperX() - 3;
            } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
                validX = cuboid.getLowerX() + 4;
            }
            if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
                validZ = cuboid.getUpperZ() - 3;
            } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
                validZ = cuboid.getLowerZ() + 4;
            }
            Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
            Vector velocity = validLoc.toVector().subtract(location.toVector()).multiply(2);
            vehicle.setVelocity(velocity);
            if (!DefaultBorderActions.lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
                player.sendMessage(ChatColor.RED + "You have reached the border!");
                DefaultBorderActions.lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Border border = FrozenBorderHandler.getBorderForWorld(event.getTo().getWorld());
        if (border != null) {
            Location location = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
            Cuboid cuboid = border.getPhysicalBounds();
            double validX = location.getX();
            double validZ = location.getZ();
            int buffer = 30;
            if (location.getBlockX() + 2 > cuboid.getUpperX()) {
                validX = cuboid.getUpperX() - buffer;
            } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
                validX = cuboid.getLowerX() + (buffer + 1);
            }
            if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
                validZ = cuboid.getUpperZ() - buffer;
            } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
                validZ = cuboid.getLowerZ() + (buffer + 1);
            }
            Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
            event.setTo(validLoc);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location toLoc = event.getTo();
        Border border = FrozenBorderHandler.getBorderForWorld(toLoc.getWorld());
        if (border == null) {
            return;
        }
        boolean to = border.contains(toLoc.getBlockX(), toLoc.getBlockZ());
        if (!to && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
        }
    }
}

