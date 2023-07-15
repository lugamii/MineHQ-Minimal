package net.lugami.basic.idle;

import net.lugami.basic.Basic;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IdleCheckRunnable extends BukkitRunnable {

    private int minutes;
    private String message;

    public IdleCheckRunnable(int minutes, String message) {
        this.minutes = minutes;
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public void run() {
        if (Basic.getInstance().getServerManager().isFrozen()) {
            return;
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            EntityPlayer player;
            if (online.hasPermission("basic.staff") || online.hasPermission("basic.idle.bypass") || online.hasMetadata("frozen") || (player = ((CraftPlayer)online).getHandle()).x() <= 0L) continue;
            long lastMoved = player.x();
            if (System.currentTimeMillis() - lastMoved < (long)(this.minutes * 60L) * 1000L) continue;
            online.kickPlayer(this.message);
        }
    }
}

