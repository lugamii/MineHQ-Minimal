package net.lugami.qlib.border;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnsureInsideRunnable extends BukkitRunnable {

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Border border = FrozenBorderHandler.getBorderForWorld(player.getWorld());
            if (border == null || player.getWorld() != border.getOrigin().getWorld() || !EnsureInsideRunnable.shouldEnsure(player) || border.contains(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) continue;
            Location location = border.correctLocation(player.getLocation());
            if (player.getVehicle() != null) {
                Entity vehicle = player.getVehicle();
                player.leaveVehicle();
                vehicle.teleport(location);
                player.teleport(location);
                vehicle.setPassenger(player);
                continue;
            }
            player.teleport(location);
        }
    }

    private static boolean isSafe(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
    }

    private static boolean shouldEnsure(Player player) {
        if (!player.hasMetadata("Border-Pushback")) {
            return true;
        }
        try {
            long pushed = player.getMetadata("Border-Pushback").get(0).asLong();
            long delta = System.currentTimeMillis() - pushed;
            return delta >= 500L;
        }
        catch (Exception e) {
            return true;
        }
    }
}

