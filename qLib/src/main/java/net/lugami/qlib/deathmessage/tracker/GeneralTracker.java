package net.lugami.qlib.deathmessage.tracker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.lugami.qlib.deathmessage.damage.PlayerDamage;
import net.lugami.qlib.deathmessage.event.CustomPlayerDamageEvent;
import net.lugami.qlib.deathmessage.FrozenDeathMessageHandler;
import net.lugami.qlib.deathmessage.damage.Damage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class GeneralTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        String message;
        switch (event.getCause().getCause()) {
            case SUFFOCATION: {
                message = "suffocated";
                break;
            }
            case DROWNING: {
                message = "drowned";
                break;
            }
            case STARVATION: {
                message = "starved to death";
                break;
            }
            case LIGHTNING: {
                message = "was struck by lightning";
                break;
            }
            case POISON: {
                message = "was poisoned";
                break;
            }
            case WITHER: {
                message = "withered away";
                break;
            }
            case CONTACT: {
                message = "was pricked to death";
                break;
            }
            case ENTITY_EXPLOSION: 
            case BLOCK_EXPLOSION: {
                message = "was blown to smithereens";
                break;
            }
            default: {
                return;
            }
        }
        List<Damage> record = FrozenDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (Damage damage : record) {
            if (damage instanceof GeneralDamageByPlayer || !(damage instanceof PlayerDamage) || knocker != null && damage.getTime() <= knockerTime) continue;
            knocker = damage;
            knockerTime = damage.getTime();
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new GeneralDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager(), message));
        } else {
            event.setTrackerDamage(new GeneralDamage(event.getPlayer().getUniqueId(), event.getDamage(), message));
        }
    }

    public static class GeneralDamageByPlayer extends PlayerDamage {

        private final String message;

        public GeneralDamageByPlayer(UUID damaged, double damage, UUID damager, String message) {
            super(damaged, damage, damager);
            this.message = message;
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + " " + ChatColor.YELLOW + this.message + " while fighting " + wrapName(this.getDamager(), getFor) + ChatColor.YELLOW + ".";
        }
    }

    public static class GeneralDamage
    extends Damage {
        private final String message;

        public GeneralDamage(UUID damaged, double damage, String message) {
            super(damaged, damage);
            this.message = message;
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return GeneralDamage.wrapName(this.getDamaged(), getFor) + " " + ChatColor.YELLOW + this.message + ".";
        }
    }

}

