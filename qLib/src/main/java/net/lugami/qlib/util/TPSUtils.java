package net.lugami.qlib.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class TPSUtils extends BukkitRunnable {

    private static int TICK_COUNT = 0;
    private static final long[] TICKS = new long[600];

    public static double getTPS() {
        return TPSUtils.getTPS(100);
    }

    public static double getTPS(int ticks) {
        try {
            if (TICK_COUNT < ticks) {
                return 20.0;
            }
            int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
            long elapsed = System.currentTimeMillis() - TICKS[target];
            return (double)ticks / ((double)elapsed / 1000.0);
        }catch (Exception e) {
            return 0;
        }
    }

    public void run() {
        TPSUtils.TICKS[TPSUtils.TICK_COUNT % TPSUtils.TICKS.length] = System.currentTimeMillis();
        ++TICK_COUNT;
    }

    public static String formatTPS(double tps, boolean color) {
        DecimalFormat format = new DecimalFormat("##.##");
        ChatColor colour;
        if (tps >= 18.0D)
        {
            colour = ChatColor.GREEN;
        }
        else
        {
            if (tps >= 15.0D) {
                colour = ChatColor.YELLOW;
            } else {
                colour = ChatColor.RED;
            }
        }
        String tpsnew = format.format(tps);
        return (color ? colour : "") + tpsnew;
    }
}

