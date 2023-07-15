package net.lugami.practice.match.listener;

import net.lugami.practice.match.Match;
import net.lugami.practice.match.event.MatchStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;

public class MatchComboListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();

        int noDamageTicks = match.getKitType().getId().contains("Combo") ? 3 : 20;
        match.getTeams().forEach(team -> team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.setMaximumNoDamageTicks(noDamageTicks)));
    }
}
