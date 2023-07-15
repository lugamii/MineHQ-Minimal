package net.lugami.basic.listener;

import net.lugami.basic.Basic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FrozenPlayerListener implements Listener {

    public static final String FROZEN_METADATA = "frozen";
    private static final String FROZEN_MESSAGE = ChatColor.RED + "You cannot do this while frozen.";

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            player.removeMetadata(FROZEN_METADATA, Basic.getInstance());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            player.removeMetadata(FROZEN_METADATA, Basic.getInstance());
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.hasPermission("basic.staff")) continue;
                otherPlayer.sendMessage("");
                otherPlayer.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + player.getName() + " logged out while frozen!");
                otherPlayer.sendMessage("");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (from.getX() != to.getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                Location newLocation = from.getBlock().getLocation().add(0.5, 0.0, 0.5);
                newLocation.setPitch(to.getPitch());
                newLocation.setYaw(to.getYaw());
                event.setTo(newLocation);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (event.getDamager().hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            ((Player)event.getDamager()).sendMessage(FROZEN_MESSAGE);
        }
        if (event.getEntity() instanceof Player && event.getEntity().hasMetadata(FROZEN_METADATA)) {
            ((Player)event.getDamager()).sendMessage(((Player)event.getEntity()).getDisplayName() + ChatColor.RED + " is currently frozen and cannot be damaged.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            player.sendMessage(FROZEN_MESSAGE);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            player.updateInventory();
            player.sendMessage(FROZEN_MESSAGE);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata(FROZEN_METADATA)) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}

