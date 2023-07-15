package net.lugami.practice.match.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.match.event.MatchStartEvent;

import java.util.ArrayList;
import java.util.List;

public class MatchBoxingListener implements Listener {

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        if ( match == null) return;
        if (!match.getKitType().getId().equals("Boxing")) return;

        for (Player player : getPlayers(match)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        Match damagerMatch = Practice.getInstance().getMatchHandler().getMatchPlaying(damager);
        if (damagerMatch == null) return;

        if (!damagerMatch.getKitType().getId().equals("Boxing")) return;

        if (damagerMatch.getTotalHits().get(damager.getUniqueId()) >= 100) {
            damaged.setHealth(0);
            damagerMatch.addSpectator(damaged, null, true);
            damaged.teleport(damaged.getLocation().add(0, 2, 0));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying((Player) event.getEntity());
        if ( match == null) return;
        if (!match.getKitType().getId().equals("Boxing")) return;

        event.setCancelled(true);

    }

    private List<Player> getPlayers(Match match) {
        List<Player> players = new ArrayList<>();

        for (MatchTeam team : match.getTeams()) {
            team.getAliveMembers().stream().map(Bukkit::getPlayer).forEach(players::add);
        }

        return players;
    }

}
