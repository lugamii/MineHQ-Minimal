package net.lugami.qlib.border;

import net.lugami.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleRunnable extends BukkitRunnable {

    private static final int RADIUS = 15;

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Border border = FrozenBorderHandler.getBorderForWorld(player.getWorld());
            if (border == null || border.getParticle() == null || !this.shouldCheck(player, border)) continue;
            for (int x = player.getLocation().getBlockX() - 15; x < player.getLocation().getBlockX() + 15; ++x) {
                for (int y = player.getLocation().getBlockY() - 5; y < player.getLocation().getBlockY() + 5; ++y) {
                    for (int z = player.getLocation().getBlockZ() - 15; z < player.getLocation().getBlockZ() + 15; ++z) {
                        Cuboid cuboid = border.getPhysicalBounds();
                        float finalX = x;
                        float finalZ = z;
                        if (x < 0) {
                            finalX += 1.0f;
                        }
                        if (z < 0) {
                            finalZ += 1.0f;
                        }
                        Location location = null;
                        if ((x > 0 && x == cuboid.getUpperX() || x < 0 && x == cuboid.getLowerX()) && (z > 0 && z <= cuboid.getUpperZ() || z < 0 && z >= cuboid.getLowerZ())) {
                            location = new Location(player.getWorld(), finalX + (x < 0 ? 0.1f : -0.1f), (float)y + 0.5f, (double)finalZ + 0.5);
                        }
                        if ((z > 0 && z == cuboid.getUpperZ() || z < 0 && z == cuboid.getLowerZ()) && (x > 0 && x <= cuboid.getUpperX() || x < 0 && x >= cuboid.getLowerX())) {
                            location = new Location(player.getWorld(), (double)finalX + 0.5, (float)y + 0.5f, finalZ + (z < 0 ? 0.1f : -0.1f));
                        }
                        if (location == null) continue;
                        player.spigot().playEffect(location, border.getParticle(), border.getMaterial().getId(), 0, 0.0f, 0.0f, 0.0f, 0.0f, 1, 15);
                    }
                }
            }
        }
    }

    private boolean shouldCheck(Player player, Border border) {
        Cuboid cuboid = border.getPhysicalBounds().clone().inset(Cuboid.CuboidDirection.HORIZONTAL, 15);
        return !this.contains(player.getLocation().getBlockX(), player.getLocation().getBlockZ(), cuboid);
    }

    public boolean contains(int x, int z, Cuboid cuboid) {
        return x >= cuboid.getLowerX() && x <= cuboid.getUpperX() && z >= cuboid.getLowerZ() && z <= cuboid.getUpperZ();
    }
}

