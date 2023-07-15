package net.lugami.qlib.deathmessage.tracker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.lugami.qlib.deathmessage.damage.PlayerDamage;
import net.lugami.qlib.deathmessage.FrozenDeathMessageHandler;
import net.lugami.qlib.deathmessage.damage.Damage;
import net.lugami.qlib.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class FallTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        List<Damage> record = FrozenDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (Damage damage : record) {
            if (damage instanceof FallDamageByPlayer || !(damage instanceof PlayerDamage) || knocker != null && damage.getTime() <= knockerTime) continue;
            knocker = damage;
            knockerTime = damage.getTime();
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new FallDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
        } else {
            event.setTrackerDamage(new FallDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class FallDamageByPlayer extends PlayerDamage {

        public FallDamageByPlayer(UUID damaged, double damage, UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " hit the ground too hard thanks to " + wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }

    public static class FallDamage extends Damage {

        public FallDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return FallDamage.wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " hit the ground too hard.";
        }
    }

}

