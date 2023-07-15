package net.lugami.qlib.autoreboot;

import com.google.common.base.Preconditions;
import net.lugami.qlib.autoreboot.listeners.AutoRebootListener;
import net.lugami.qlib.autoreboot.tasks.SilentServerRebootTask;
import net.lugami.qlib.command.FrozenCommandHandler;
import net.lugami.qlib.qLib;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SilentAutoRebootHandler {

    private static List<Integer> rebootTimes;
    private static boolean initiated = false;
    private static SilentServerRebootTask serverRebootTask = null;

    private SilentAutoRebootHandler() {
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        FrozenCommandHandler.registerPackage(qLib.getInstance(), "net.lugami.qlib.autoreboot.commands");
        rebootTimes = qLib.getInstance().getConfig().getIntegerList("AutoRebootTimes");
        qLib.getInstance().getServer().getPluginManager().registerEvents(new AutoRebootListener(), qLib.getInstance());
    }

    @Deprecated
    public static void rebootServer(int seconds) {
        SilentAutoRebootHandler.rebootServer(seconds, TimeUnit.SECONDS);
    }

    public static void rebootServer(int timeUnitAmount, TimeUnit timeUnit) {
        if (serverRebootTask != null) {
            throw new IllegalStateException("Reboot already in progress");
        }
        serverRebootTask = new SilentServerRebootTask(timeUnitAmount, timeUnit);
        serverRebootTask.runTaskTimer(qLib.getInstance(), 20L, 20L);
    }

    public static boolean isRebooting() {
        return serverRebootTask != null;
    }

    public static int getRebootSecondsRemaining() {
        if (serverRebootTask == null) {
            return -1;
        }
        return serverRebootTask.getSecondsRemaining();
    }

    public static void cancelReboot() {
        if (serverRebootTask != null) {
            serverRebootTask.cancel();
            serverRebootTask = null;
        }
    }

    public static List<Integer> getRebootTimes() {
        return rebootTimes;
    }

    public static boolean isInitiated() {
        return initiated;
    }

}

