package net.lugami.practice.match.listener;

import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchState;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.match.event.MatchCountdownStartEvent;
import net.lugami.practice.match.event.MatchTerminateEvent;
import net.lugami.practice.util.CheatBreakerKey;
import net.lugami.practice.util.FireworkEffectPlayer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class MatchWizardListener implements Listener {

    private final FireworkEffectPlayer fireworkEffectPlayer = new FireworkEffectPlayer();

    private static final long FIREBALL_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private final Map<UUID, Long> fireballCooldown = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    public void onPlayerWand(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.DIAMOND_HOE || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().contains("WIZARD")) {
            return;
        }

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.BLUE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();

        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setShooter(player);
        snowball.setVelocity(snowball.getVelocity().multiply(2));

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 100) {
                    cancel();
                    return;
                }

                if (snowball.isDead() || snowball.isOnGround()) {
                    for (Entity entity : snowball.getNearbyEntities(4, 4, 4)) {
                        MatchTeam entityTeam = match.getTeam(entity.getUniqueId());

                        if (entityTeam != null && !entityTeam.getAllMembers().contains(player.getUniqueId())) {
                            entity.setVelocity(entity.getLocation().toVector().subtract(snowball.getLocation().toVector()).normalize().add(new Vector(0, 0.7, 0.0)));
                        }
                    }

                    snowball.remove();
                    cancel();
                } else {
                    try {
                        fireworkEffectPlayer.playFirework(snowball.getWorld(), snowball.getLocation().add(0, 1, 0), effect);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.runTaskTimer(Practice.getInstance(), 1L, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    // no ignoreCancelled = true because right click on air
    // events are by default cancelled (wtf Bukkit)
    public void onPlayerFireball(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.BLAZE_ROD || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);

        if (match == null || !match.getKitType().getId().contains("WIZARD")) {
            return;
        }

        if (match.getState() == MatchState.COUNTDOWN) {
            return;
        }

        long cooldownExpires = fireballCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (cooldownExpires > System.currentTimeMillis()) {
            int millisLeft = (int) (cooldownExpires - System.currentTimeMillis());
            double secondsLeft = millisLeft / 1000D;
            // round to 1 digit past decimal
            secondsLeft = Math.round(10D * secondsLeft) / 10D;

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + secondsLeft + ChatColor.RED + " seconds!");
            return;
        }

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setShooter(player);
        fireball.setIsIncendiary(false);
        fireball.setYield(0);
        fireball.setVelocity(fireball.getVelocity().multiply(1.25));

        match.playSoundAll(Sound.GHAST_FIREBALL, 1F);

        fireballCooldown.put(player.getUniqueId(), System.currentTimeMillis() + FIREBALL_COOLDOWN_MILLIS);
        CheatBreakerKey.FIRE_BALL.send(player, FIREBALL_COOLDOWN_MILLIS);

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 100) {
                    cancel();
                    return;
                }

                if (fireball.isDead() || fireball.isOnGround()) {
                    for (Entity entity : fireball.getNearbyEntities(4, 4, 4)) {
                        MatchTeam entityTeam = match.getTeam(entity.getUniqueId());

                        if (entityTeam != null && !entityTeam.getAllMembers().contains(player.getUniqueId())) {
                            entity.setVelocity(entity.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize().add(new Vector(0, 0.3, 0.0)));
//                            entity.setVelocity(entity.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize().multiply(1.1));
                            entity.setFireTicks(60);
                        }
                    }

                    fireball.remove();
                    cancel();
                } else {
                    try {
                        fireworkEffectPlayer.playFirework(fireball.getWorld(), fireball.getLocation().add(0, 1, 0), effect);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.runTaskTimer(Practice.getInstance(), 1L, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        fireballCooldown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // When players die, their enderpearls are still left on the map,
        // allowing players to teleport after they die
        for (Fireball fireball : player.getWorld().getEntitiesByClass(Fireball.class)) {
            if (fireball.getShooter() == player) {
                fireball.remove();
            }
        }

        fireballCooldown.remove(player.getUniqueId());
        CheatBreakerKey.FIRE_BALL.clear(player);;
    }

    // reset pearl cooldowns when ending a match
    // this is only so (most) players don't see the cooldown
    // in the player - the 'actual' reset is the one prior to
    // start a match, as with this we can 'forget' players who
    // died (and aren't alive anymore) right before the end of
    // a match.
    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        for (MatchTeam team : event.getMatch().getTeams()) {
            team.getAliveMembers().forEach(fireballCooldown::remove);
            team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(CheatBreakerKey.ENDER_PEARL::clear);
        }
    }

    // see comment on #onMatchTerminate(MatchTerminateEvent)
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        for (MatchTeam team : event.getMatch().getTeams()) {
            team.getAllMembers().forEach(fireballCooldown::remove);
            team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(CheatBreakerKey.ENDER_PEARL::clear);
        }
    }
}