package net.lugami.qlib.util.countdown;

import net.lugami.qlib.qLib;
import net.lugami.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Countdown extends BukkitRunnable {

    private final String broadcastMessage;
    private final int[] broadcastAt;
    private final Runnable tickHandler;
    private final Runnable broadcastHandler;
    private final Runnable finishHandler;
    private final Predicate<Player> messageFilter;
    private int seconds;
    private boolean first = true;

    public static CountdownBuilder of(int amount, TimeUnit unit) {
        return new CountdownBuilder((int)unit.toSeconds(amount));
    }

    Countdown(int seconds, String broadcastMessage, Runnable tickHandler, Runnable broadcastHandler, Runnable finishHandler, Predicate<Player> messageFilter, int ... broadcastAt) {
        this.seconds = seconds;
        this.broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
        this.broadcastAt = broadcastAt;
        this.tickHandler = tickHandler;
        this.broadcastHandler = broadcastHandler;
        this.finishHandler = finishHandler;
        this.messageFilter = messageFilter;
        this.runTaskTimer(qLib.getInstance(), 0L, 20L);
    }

    public final void run() {
        if (!this.first) {
            --this.seconds;
        } else {
            this.first = false;
        }
        for (int index : this.broadcastAt) {
            if (this.seconds != index) continue;
            String message = this.broadcastMessage.replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (this.messageFilter != null && !this.messageFilter.test(player)) continue;
                player.sendMessage(message);
            }
            if (this.broadcastHandler == null) continue;
            this.broadcastHandler.run();
        }
        if (this.seconds == 0) {
            if (this.finishHandler != null) {
                this.finishHandler.run();
            }
            this.cancel();
        } else if (this.tickHandler != null) {
            this.tickHandler.run();
        }
    }

    public int getSecondsRemaining() {
        return this.seconds;
    }
}

