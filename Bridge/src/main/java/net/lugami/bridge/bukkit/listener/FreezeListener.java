package net.lugami.bridge.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.MetadataValue;

public class FreezeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        boolean whitelistedCommand = command.startsWith("/freezeserver") || command.startsWith("/auth") || command.startsWith("/register") || command.startsWith("/2fasetup") || command.startsWith("/setup2fa");
        if (!whitelistedCommand && event.getPlayer().hasMetadata("Locked")) {
            event.getPlayer().sendMessage((event.getPlayer().getMetadata("Locked").get(0)).asString());
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.getPlayer().sendMessage((event.getPlayer().getMetadata("Locked").get(0)).asString());
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getDamager().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.getPlayer().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata("Locked")) {
            event.setCancelled(true);
        }

    }

}
