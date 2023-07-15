package net.lugami.qlib.tab;

import net.lugami.qlib.qLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListener
implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        new BukkitRunnable(){

            public void run() {
                FrozenTabHandler.addPlayer(event.getPlayer());
            }
        }.runTaskLater(qLib.getInstance(), 5);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        FrozenTabHandler.removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }

}

