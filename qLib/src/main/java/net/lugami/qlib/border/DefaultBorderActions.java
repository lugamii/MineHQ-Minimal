package net.lugami.qlib.border;

import com.google.common.collect.Maps;
import net.lugami.qlib.border.event.BorderChangeEvent;
import net.lugami.qlib.border.event.PlayerExitBorderEvent;
import net.lugami.qlib.cuboid.Cuboid;
import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class DefaultBorderActions {

    static Map<UUID, Long> lastMessaged;
    public static final Consumer<PlayerExitBorderEvent> CANCEL_EXIT;
    public static final Consumer<PlayerExitBorderEvent> PUSHBACK_ON_EXIT;
    public static final Consumer<BorderChangeEvent> ENSURE_PLAYERS_IN_BORDER;

    static {
        DefaultBorderActions.lastMessaged = Maps.newHashMap();
        final Player[] player = new Player[1];
        CANCEL_EXIT = (playerExitBorderEvent -> {
            player[0] = playerExitBorderEvent.getPlayer();
            playerExitBorderEvent.setCancelled(true);
            if (!DefaultBorderActions.lastMessaged.containsKey(player[0].getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.lastMessaged.get(player[0].getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
                player[0].sendMessage(ChatColor.RED + "You have reached the border!");
                DefaultBorderActions.lastMessaged.put(player[0].getUniqueId(), System.currentTimeMillis());
            }
        });
        PUSHBACK_ON_EXIT = (playerExitBorderEvent -> {
            playerExitBorderEvent.getPlayer().setMetadata("Border-Pushback", new FixedMetadataValue(qLib.getInstance(), System.currentTimeMillis()));
            new BukkitRunnable() {
                public void run() {
                    final Border border = playerExitBorderEvent.getBorder();
                    final Player player = playerExitBorderEvent.getPlayer();
                    final Location location = playerExitBorderEvent.getTo();
                    final Cuboid cuboid = border.getPhysicalBounds();
                    double validX = location.getX();
                    double validZ = location.getZ();
                    if (location.getBlockX() + 2 > cuboid.getUpperX()) {
                        validX = cuboid.getUpperX() - 3;
                    }
                    else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
                        validX = cuboid.getLowerX() + 4;
                    }
                    if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
                        validZ = cuboid.getUpperZ() - 3;
                    }
                    else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
                        validZ = cuboid.getLowerZ() + 4;
                    }
                    final Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
                    final Vector velocity = validLoc.toVector().subtract(playerExitBorderEvent.getTo().toVector()).multiply(0.18);
                    if (player.getVehicle() != null) {
                        player.getVehicle().setVelocity(velocity);
                    }
                    else {
                        player.setVelocity(velocity);
                    }
                    if (!DefaultBorderActions.lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - DefaultBorderActions.lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
                        player.sendMessage(ChatColor.RED + "You have reached the border!");
                        DefaultBorderActions.lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }.runTask(qLib.getInstance());
        });
        final Border[] border = new Border[1];
        final Iterator<Player> iterator = (Iterator<Player>) Bukkit.getOnlinePlayers().iterator();
        final Player[] player2 = new Player[1];
        final Location[] location = new Location[1];
        final Entity[] vehicle = new Entity[1];
        ENSURE_PLAYERS_IN_BORDER = (borderChangeEvent -> {
            border[0] = borderChangeEvent.getBorder();

            while (iterator.hasNext()) {
                player2[0] = iterator.next();
                if (player2[0].getWorld() != border[0].getOrigin().getWorld()) {
                    continue;
                }
                else if (!border[0].contains(player2[0].getLocation().getBlockX(), player2[0].getLocation().getBlockZ())) {
                    location[0] = border[0].correctLocation(player2[0].getLocation());
                    if (player2[0].getVehicle() != null) {
                        vehicle[0] = player2[0].getVehicle();
                        player2[0].leaveVehicle();
                        vehicle[0].teleport(location[0]);
                        player2[0].teleport(location[0]);
                        vehicle[0].setPassenger(player2[0]);
                    }
                    else {
                        player2[0].teleport(location[0]);
                    }
                }
                else {
                    continue;
                }
            }
        });
    }
}