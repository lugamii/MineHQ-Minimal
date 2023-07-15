package net.lugami.practice.kit.listener;

import net.lugami.practice.Practice;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class KitLoadListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST runs first
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Practice.getInstance().getKitHandler().loadKits(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // MONITOR runs last
    public void onPlayerQuit(PlayerQuitEvent event) {
        Practice.getInstance().getKitHandler().unloadKits(event.getPlayer());
    }

}