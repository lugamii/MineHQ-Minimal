package net.lugami.practice.rematch.listener;

import net.lugami.practice.Practice;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RematchUnloadListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Practice.getInstance().getRematchHandler().unloadRematchData(event.getPlayer());
    }

}