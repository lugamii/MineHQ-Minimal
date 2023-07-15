package net.lugami.basic.listener;

import net.lugami.basic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FrozenServerListener implements Listener {

    private static final String DENY_MESSAGE = ChatColor.RED + "The server is currently frozen.";

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("basic.staff")) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Location newTo = event.getFrom().getBlock().getLocation().clone().add(0.5, 0.0, 0.5);
        newTo.setPitch(event.getTo().getPitch());
        newTo.setYaw(event.getTo().getYaw());
        event.setTo(newTo);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Basic.getInstance().getServerManager().isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        if (event.getDamager() instanceof Player) {
            Player player = (Player)event.getDamager();
            if (player.hasPermission("basic.staff")) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(DENY_MESSAGE);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("basic.staff")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(DENY_MESSAGE);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Basic.getInstance().getServerManager().isFrozen()) {
            event.getPlayer().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "The server is currently frozen.");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("basic.staff") || event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        event.setCancelled(true);
        event.setTo(event.getFrom());
        player.sendMessage(DENY_MESSAGE);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("basic.staff")) {
            return;
        }
        event.setCancelled(true);
        player.updateInventory();
        player.sendMessage(DENY_MESSAGE);
    }
}

