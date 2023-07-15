package net.lugami.practice.match.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchState;

public class MatchFreezeListener implements Listener {

    @EventHandler
    public void onCountdownEnd(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().equals("SUMO") || match.getState() != MatchState.COUNTDOWN) return;

        event.getPlayer().teleport(from);
    }

}
