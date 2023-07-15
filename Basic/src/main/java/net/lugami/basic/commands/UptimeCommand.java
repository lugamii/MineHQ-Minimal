package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class UptimeCommand {

    public static String uptimeColor(int secs) {
        if ((long)secs <= TimeUnit.HOURS.toSeconds(16L)) {
            return "§a";
        }
        if ((long)secs <= TimeUnit.HOURS.toSeconds(24L)) {
            return "§e";
        }
        return "§c";
    }

    @Command(names={"uptime"}, permission="basic.uptime", description = "Check how long the server has been up for")
    public static void uptime(CommandSender sender) {
        int seconds = (int)((System.currentTimeMillis() - Basic.getInstance().getStartupTime()) / 1000L);
        sender.sendMessage(ChatColor.GOLD + "The server has been running for " + UptimeCommand.uptimeColor(seconds) + TimeUtils.formatIntoDetailedString(seconds) + ChatColor.GOLD + ".");
    }
}

