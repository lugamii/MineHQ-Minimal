package net.lugami.bridge.global.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.lugami.bridge.bukkit.Bridge;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.ThreadFactory;

public class Tasks {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Runnable runnable, boolean async) {
        if(async) {
            Bridge.getInstance().getServer().getScheduler().runTaskAsynchronously(Bridge.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }

    public static void run(Runnable runnable) {
        Bridge.getInstance().getServer().getScheduler().runTask(Bridge.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        Bridge.getInstance().getServer().getScheduler().runTaskAsynchronously(Bridge.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, long delay) {
        Bridge.getInstance().getServer().getScheduler().runTaskLater(Bridge.getInstance(), runnable, delay);
    }

    public static void runAsyncLater(Runnable runnable, long delay) {
        Bridge.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Bridge.getInstance(), runnable, delay);
    }

    public static void runTimer(Runnable runnable, long delay, long interval) {
        Bridge.getInstance().getServer().getScheduler().runTaskTimer(Bridge.getInstance(), runnable, delay, interval);
    }

    public static void runAsyncTimer(Runnable runnable, long delay, long interval) {
        Bridge.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Bridge.getInstance(), runnable, delay, interval);
    }

    public static BukkitScheduler getScheduler() {
        return Bridge.getInstance().getServer().getScheduler();
    }
}
