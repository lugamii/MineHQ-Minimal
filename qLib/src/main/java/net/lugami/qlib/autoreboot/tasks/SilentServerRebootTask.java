package net.lugami.qlib.autoreboot.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.qlib.qLib;

import java.util.concurrent.TimeUnit;

public final class SilentServerRebootTask extends BukkitRunnable {

    private int secondsRemaining;
    private final boolean wasWhitelisted;

    public SilentServerRebootTask(int timeUnitAmount, TimeUnit timeUnit) {
        this.secondsRemaining = (int)timeUnit.toSeconds(timeUnitAmount);
        this.wasWhitelisted = qLib.getInstance().getServer().hasWhitelist();
    }

    public void run() {
        if (this.secondsRemaining == 300) {
            qLib.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 0) {
            qLib.getInstance().getServer().setWhitelist(this.wasWhitelisted);
            qLib.getInstance().getServer().shutdown();
        }
        switch (this.secondsRemaining) {
            case 5: 
            case 10: 
            case 15: 
            case 30: 
            case 60: 
            case 120: 
            case 180: 
            case 240: 
            case 300: {
                break;
            }
        }
        --this.secondsRemaining;
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        Bukkit.setWhitelist(this.wasWhitelisted);
    }

    public int getSecondsRemaining() {
        return this.secondsRemaining;
    }
}

