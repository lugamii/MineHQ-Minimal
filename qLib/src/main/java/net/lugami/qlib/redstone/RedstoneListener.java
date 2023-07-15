package net.lugami.qlib.redstone;

import net.lugami.qlib.qLib;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedstoneListener implements Listener {

    @Getter private final Map<UUID, Long> leverCooldown = new HashMap<>();

    private boolean isOnCooldown(UUID uuid) {
        return getRemainingCooldown(uuid) > 0L;
    }

    private long getRemainingCooldown(UUID uuid) {
        if(leverCooldown.get(uuid) == null) return 0L;
        return Math.max(0L, leverCooldown.get(uuid) - System.currentTimeMillis());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.LEVER) return;
        if(isOnCooldown(player.getUniqueId())) event.setCancelled(true);
        else leverCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 500);
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
        if(Bukkit.spigot().getTPS()[0] < 19.5) {
            event.setNewCurrent(0);
            Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> event.getBlock().setType(Material.AIR),1);
        }
    }

}
