package net.lugami.practice.lobby.listener;

import net.lugami.practice.party.event.PartyCreateEvent;
import net.lugami.practice.party.event.PartyMemberJoinEvent;
import net.lugami.practice.Practice;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class LobbySpecModeListener implements Listener {

    @EventHandler
    public void onPartyMemberJoin(PartyMemberJoinEvent event) {
        Practice.getInstance().getLobbyHandler().setSpectatorMode(event.getMember(), false);
    }

    @EventHandler
    public void onPartyCreate(PartyCreateEvent event) {
        Player leader = Bukkit.getPlayer(event.getParty().getLeader());
        Practice.getInstance().getLobbyHandler().setSpectatorMode(leader, false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Practice.getInstance().getLobbyHandler().setSpectatorMode(event.getPlayer(), false);
    }

}