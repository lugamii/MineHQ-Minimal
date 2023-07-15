package net.lugami.practice.postmatchinv.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.practice.Practice;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.match.event.MatchCountdownStartEvent;
import net.lugami.practice.match.event.MatchTerminateEvent;
import net.lugami.practice.postmatchinv.PostMatchInvHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PostMatchInvGeneralListener implements Listener {

    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        postMatchInvHandler.recordMatch(event.getMatch());
        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                event.getMatch().messageAll(ChatColor.GOLD + "Match link: https://www." + Practice.getInstance().getNetworkWebsite() + "/match/" + event.getMatch().get_id());
            }
        }, 10L);
    }


    // remove 'old' post match data when their match starts
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                postMatchInvHandler.removePostMatchData(member);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        UUID playerUuid = event.getPlayer().getUniqueId();

        postMatchInvHandler.removePostMatchData(playerUuid);
    }

}