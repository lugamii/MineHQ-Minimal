package net.lugami.practice.duel.listener;

import net.lugami.practice.match.event.MatchCountdownStartEvent;
import net.lugami.practice.match.event.MatchSpectatorJoinEvent;
import net.lugami.practice.party.event.PartyDisbandEvent;
import net.lugami.practice.Practice;
import net.lugami.practice.duel.DuelHandler;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class DuelListener implements Listener {

    @EventHandler
    public void onMatchSpectatorJoin(MatchSpectatorJoinEvent event) {
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        Player player = event.getSpectator();

        duelHandler.removeInvitesTo(player);
        duelHandler.removeInvitesFrom(player);
    }

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        Party party = event.getParty();

        duelHandler.removeInvitesTo(party);
        duelHandler.removeInvitesFrom(party);
    }

    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        for (MatchTeam team : event.getMatch().getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberPlayer = Bukkit.getPlayer(member);

                duelHandler.removeInvitesTo(memberPlayer);
                duelHandler.removeInvitesFrom(memberPlayer);
            }
        }
    }

}