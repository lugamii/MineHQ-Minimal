package net.lugami.practice.match.listener;

import org.bukkit.entity.*;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.UUID;

public class MatchStatsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        Match damagerMatch = Practice.getInstance().getMatchHandler().getMatchPlaying(damager);
        if (damagerMatch == null) return;

        Map<UUID, UUID> lastHitMap = damagerMatch.getLastHit();
        Map<UUID, Integer> combos = damagerMatch.getCombos();
        Map<UUID, Integer> totalHits = damagerMatch.getTotalHits();
        Map<UUID, Integer> longestCombo = damagerMatch.getLongestCombo();

        damagerMatch.setLastHitLocation(damager.getLocation());

        UUID lastHit = lastHitMap.put(damager.getUniqueId(), damaged.getUniqueId());
        if (lastHit != null) {
            if (lastHit.equals(damaged.getUniqueId())) {
                combos.put(damager.getUniqueId(), combos.getOrDefault(damager.getUniqueId(), 0) + 1);
            } else {
                combos.put(damager.getUniqueId(), 1);
            }

            longestCombo.put(damager.getUniqueId(), Math.max(combos.get(damager.getUniqueId()), longestCombo.getOrDefault(damager.getUniqueId(), 1)));
        } else {
            combos.put(damager.getUniqueId(), 0);
        }

        totalHits.put(damager.getUniqueId(), totalHits.getOrDefault(damager.getUniqueId(), 0) + 1);
        while (lastHitMap.values().remove(damager.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionLaunch(ProjectileLaunchEvent event) {
        Projectile thrownEntity = event.getEntity();
        if (!(thrownEntity instanceof ThrownPotion)) return;

        ThrownPotion thrownPotion = (ThrownPotion) thrownEntity;

        ProjectileSource projectileSource = thrownPotion.getShooter();
        if (!(projectileSource instanceof Player)) return;

        Player player = (Player) projectileSource;
        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying(player);

        if (match == null) return;

        if (thrownPotion.getItem().getDurability() == 16421 || thrownPotion.getItem().getDurability() == 16385) {
            match.getThrownPots().put(player.getUniqueId(), match.getThrownPots().getOrDefault(player.getUniqueId(), 0) + 1);
        } else if (thrownPotion.getItem().getDurability() == 16388 || thrownPotion.getItem().getDurability() == 16426 || thrownPotion.getItem().getDurability() == 16424 || thrownPotion.getItem().getDurability() == 16428 || thrownPotion.getItem().getDurability() == 16458 || thrownPotion.getItem().getDurability() == 16420) {
            match.getThrownDebuffs().put(player.getUniqueId(), match.getThrownPots().getOrDefault(player.getUniqueId(), 0) + 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSplashPotion(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getEntity();

        ProjectileSource projectileSource = thrownPotion.getShooter();
        if (!(projectileSource instanceof Player)) return;

        Player player = (Player) projectileSource;
        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying(player);

        if (match == null) return;

        if (thrownPotion.getItem().getDurability() == 16421 || thrownPotion.getItem().getDurability() == 16385) {
            if (event.getIntensity(player) <= 0.65D) {
                match.getMissedPots().put(player.getUniqueId(), Math.max(match.getMissedPots().getOrDefault(player.getUniqueId(), 1) + 1, 0));
            }
        } else if (thrownPotion.getItem().getDurability() == 16388 || thrownPotion.getItem().getDurability() == 16426 || thrownPotion.getItem().getDurability() == 16424 || thrownPotion.getItem().getDurability() == 16428 | thrownPotion.getItem().getDurability() == 16458 || thrownPotion.getItem().getDurability() == 16420) {
            for (Entity e : thrownPotion.getNearbyEntities(thrownPotion.getLocation().getX(), thrownPotion.getLocation().getY(), thrownPotion.getLocation().getZ())) {
                for (UUID u : match.getTeam(player.getUniqueId()).getAliveMembers()) {
                    if (e.getUniqueId() != u) {
                        if (event.getIntensity((Player)e) <= 0.50D) {
                            match.getMissedDebuffs().put(player.getUniqueId(), Math.max(match.getMissedDebuffs().getOrDefault(player.getUniqueId(), 1) + 1, 0));
                        }
                    }
                }
            }
        }
    }
/*
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        match.getTeams().forEach(team -> {
            if (match.getWinner() == team) {
                team.getAllMembers().forEach(Practice.getInstance().getWinsMap()::incrementWins);
            } else {
                team.getAllMembers().forEach(Practice.getInstance().getLossMap()::incrementLosses);
            }
        });
    }
    */
}