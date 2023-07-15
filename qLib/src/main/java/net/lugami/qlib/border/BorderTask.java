package net.lugami.qlib.border;

import net.lugami.qlib.border.event.BorderChangeEvent;
import net.lugami.qlib.cuboid.Cuboid;
import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class BorderTask extends BukkitRunnable {

    private final Border border;
    private int borderChange = 5;
    private long borderChangeDelay = 10L;
    private BorderAction action = BorderAction.NONE;
    private int tracker = 0;

    public BorderTask(Border border) {
        this.border = border;
        this.runTaskTimer(qLib.getInstance(), 1L, 1L);
    }

    public void run() {
        int seconds;
        if (this.action != BorderAction.NONE) {
            ++this.tracker;
        }
        if (this.border != null && this.tracker % 20 == 0 && (long) (this.tracker / 20) % this.borderChangeDelay == 0L) {
            Cuboid previous;
            int previousSize = this.border.getSize();
            switch (this.action) {
                case SHRINK: {
                    previous = this.border.contract(this.borderChange);
                    break;
                }
                case GROW: {
                    previous = this.border.expand(this.borderChange);
                    break;
                }
                default: {
                    return;
                }
            }
            Bukkit.getPluginManager().callEvent(new BorderChangeEvent(this.border, previousSize, previous, this.action));
            this.border.fill();
        }
    }

    public void start() {
    }

    public BorderTask setAction(BorderAction action) {
        this.action = action;
        this.tracker = 0;
        return this;
    }

    public BorderTask setBorderChangeDelay(long time, TimeUnit timeUnit) {
        this.borderChangeDelay = timeUnit.toSeconds(time);
        this.tracker = 0;
        return this;
    }

    public Border getBorder() {
        return this.border;
    }

    public int getBorderChange() {
        return this.borderChange;
    }

    public long getBorderChangeDelay() {
        return this.borderChangeDelay;
    }

    public BorderAction getAction() {
        return this.action;
    }

    public boolean isFirst() {
        return true;
    }

    public int getTracker() {
        return this.tracker;
    }

    public BorderTask setBorderChange(int borderChange) {
        this.borderChange = borderChange;
        return this;
    }

    public enum BorderAction {
        SHRINK,
        GROW,
        SET,
        NONE

    }

}

