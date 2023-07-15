package net.lugami.qlib.util;

import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.qlib.qLib;

public class TaskUtil {

    public static void run(Runnable runnable) {
        qLib.getInstance().getServer().getScheduler().runTask(qLib.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        qLib.getInstance().getServer().getScheduler().runTaskTimer(qLib.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(qLib.getInstance(), delay, timer);
    }

    public static void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimerAsynchronously(qLib.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        qLib.getInstance().getServer().getScheduler().runTaskLater(qLib.getInstance(), runnable, delay);
    }

    public static void runAsync(Runnable runnable) {
        qLib.getInstance().getServer().getScheduler().runTaskAsynchronously(qLib.getInstance(), runnable);
    }

}

