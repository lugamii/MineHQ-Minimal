package org.spigotmc;

import org.apache.commons.lang.StringUtils;
import net.jafama.FastMath;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command
{

    public TicksPerSecondCommand(String name)
    {
        super( name );
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission( "" );
    }

    public static String format(double tps) {
        return ((tps > 18.0) ? ChatColor.DARK_GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString()
                + ((tps > 20.0) ? "*" : "") + FastMath.min(FastMath.round(tps * 100.0) / 100.0, 20.0);
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if (!testPermission(sender)) {
            return true;
        }

        if (sender.hasPermission("bukkit.command.tps.advanced")) {
            double[] tps = Bukkit.spigot().getTPS();
            String[] tpsAvg = new String[tps.length];

            for (int i = 0; i < tps.length; i++) {
                tpsAvg[i] = formatAdvancedTps(tps[i]);
            }

            int entities = MinecraftServer.getServer().entities;
            int activeEntities = MinecraftServer.getServer().activeEntities;
            double activePercent = Math.round(10000.0 * activeEntities / entities) / 100.0;

            sender.sendMessage(ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " + StringUtils.join(tpsAvg, ", "));
            sender.sendMessage(ChatColor.GOLD + "Full tick: " + formatTickTime(MinecraftServer.getServer().lastTickTime) + " ms");
            sender.sendMessage(ChatColor.GOLD + "Active entities: " + ChatColor.GREEN + activeEntities + "/" + entities + " (" + activePercent + "%)");
            sender.sendMessage(ChatColor.GOLD + "Online players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        } else {
            double tps = Bukkit.spigot().getTPS()[1];
            StringBuilder tpsBuilder = new StringBuilder();

            tpsBuilder.append(ChatColor.GOLD).append("Server performance: ");
            tpsBuilder.append(formatBasicTps(tps)).append(ChatColor.GOLD).append("/20.0");
            tpsBuilder.append(" [").append(tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED);

            int i = 0;

            for (; i < Math.round(tps); i++) {
                tpsBuilder.append("|");
            }

            tpsBuilder.append(ChatColor.DARK_GRAY);

            for (; i < 20; i++) {
                tpsBuilder.append("|");
            }

            tpsBuilder.append(ChatColor.GOLD).append("]");
            sender.sendMessage(tpsBuilder.toString());
        }

        return true;
    }

    private static String formatTickTime(double time) {
        return (time < 40.0D ? ChatColor.GREEN : time < 60.0D ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.round(time * 10.0D) / 10.0D;
    }

    private static String formatAdvancedTps(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 100.0D) / 100.0, 20.0);
    }

    private String formatBasicTps(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 10.0D) / 10.0D, 20.0D);
    }

}
