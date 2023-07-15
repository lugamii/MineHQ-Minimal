package net.lugami.practice.setting.listener;

import net.lugami.practice.Practice;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SettingLoadListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // LOWEST runs first
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Practice.getInstance().getSettingHandler().loadSettings(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR) // MONITOR runs last
    public void onPlayerQuit(PlayerQuitEvent event) {
        Practice.getInstance().getSettingHandler().unloadSettings(event.getPlayer());
    }

}