package net.lugami.practice.lobby.listener;

import net.lugami.qlib.menu.Menu;
import net.lugami.practice.lobby.LobbyHandler;
import net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public final class LobbyGeneralListener implements Listener {

    private final LobbyHandler LobbyHandler;

    public LobbyGeneralListener(LobbyHandler LobbyHandler) {
        this.LobbyHandler = LobbyHandler;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        LobbyParkourListener.Parkour parkour = LobbyParkourListener.getParkourMap().get(event.getPlayer().getUniqueId());
        if (parkour != null && parkour.getCheckpoint() != null) {
            event.setSpawnLocation(parkour.getCheckpoint().getLocation());
            return;
        }

        event.setSpawnLocation(LobbyHandler.getLobbyLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LobbyHandler.returnToLobby(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (LobbyHandler.isInLobby(player)) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                LobbyHandler.returnToLobby(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (LobbyHandler.isInLobby((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (LobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!LobbyHandler.isInLobby(player)) {
            return;
        }

        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        // just remove the item for players in these menus, so they can 'drop' items to remove them
        // same thing for admins in build mode, just pretend to drop the item
        if (player.hasMetadata("Build") || (openMenu != null && openMenu.isNoncancellingInventory())) {
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    // cancel inventory interaction in the player except for menus
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!LobbyHandler.isInLobby(clicked) || clicked.hasMetadata("Build") || Menu.currentlyOpenedMenus.containsKey(clicked.getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player clicked = (Player) event.getWhoClicked();

        if (!LobbyHandler.isInLobby(clicked) || clicked.hasMetadata("Build") || Menu.currentlyOpenedMenus.containsKey(clicked.getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (LobbyHandler.isInLobby(event.getEntity())) {
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder inventoryHolder = event.getSource().getHolder();

        if (inventoryHolder instanceof Player) {
            Player player = (Player) inventoryHolder;

            if (!LobbyHandler.isInLobby(player) || Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();

        if (LobbyHandler.isInLobby(event.getPlayer()) && gameMode != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (LobbyHandler.isInLobby(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCheatBreaker(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(new PacketPlayOutCustomPayload("REGISTER", "CB-Binary".getBytes()));
    }
}