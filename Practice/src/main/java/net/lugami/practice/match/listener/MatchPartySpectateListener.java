package net.lugami.practice.match.listener;

import net.lugami.practice.party.event.PartyMemberJoinEvent;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MatchPartySpectateListener implements Listener {

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match leaderMatch = matchHandler.getMatchPlayingOrSpectating(Bukkit.getPlayer(event.getParty().getLeader()));

        if (leaderMatch != null) {
            leaderMatch.addSpectator(event.getMember(), null);
        }
    }

}