package net.lugami.practice.rematch.listener;

import net.lugami.practice.Practice;
import net.lugami.practice.match.event.MatchTerminateEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class RematchGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Practice.getInstance().getRematchHandler().registerRematches(event.getMatch());
    }

}