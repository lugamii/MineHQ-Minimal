package net.lugami.basic.listener;

import net.lugami.basic.util.inventory.BasicPlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BasicInventoryListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (BasicPlayerInventory.getStorage().containsKey(player.getUniqueId())) {
            BasicPlayerInventory.getStorage().get(player.getUniqueId()).onJoin(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (BasicPlayerInventory.getStorage().containsKey(player.getUniqueId())) {
            BasicPlayerInventory.getStorage().get(player.getUniqueId()).onQuit();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (BasicPlayerInventory.getOpen().contains(player.getUniqueId()) && !player.hasPermission("basic.invsee.edit")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to edit this inventory.");
        }
    }
}

