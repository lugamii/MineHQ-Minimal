package net.lugami.qlib.deathmessage.tracker;

import java.util.UUID;

import net.lugami.qlib.deathmessage.damage.MobDamage;
import net.lugami.qlib.deathmessage.event.CustomPlayerDamageEvent;
import net.lugami.qlib.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class EntityTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        Entity damager = damageByEntityEvent.getDamager();
        if (damager instanceof LivingEntity && !(damager instanceof Player)) {
            event.setTrackerDamage(new EntityDamage(event.getPlayer().getUniqueId(), event.getDamage(), damager));
        }
    }

    public static class EntityDamage extends MobDamage {

        public EntityDamage(UUID damaged, double damage, Entity entity) {
            super(damaged, damage, entity.getType());
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return wrapName(this.getDamaged(), getFor) + ChatColor.YELLOW + " was slain by a " + ChatColor.RED + EntityUtils.getName(this.getMobType()) + ChatColor.YELLOW + ".";
        }
    }

}

