package net.lugami.qlib.command.defaults;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.qLib;
import net.lugami.qlib.util.TimeUtils;

import java.util.Date;

public class LastErrorCommand {

    @Command(names = "lasterror", permission = "qlib.lasterror", hidden = true)
    public static void lastError(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&5&m---------------------------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6qLib: &7❘ &4Error Management"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7&m---------------------------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eLocal Redis Error: &c" + TimeUtils.formatIntoCalendarString(new Date(qLib.getInstance().getLocalRedisLastError()))));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eBackbone Redis Error: &c" + TimeUtils.formatIntoCalendarString(new Date(qLib.getInstance().getBackboneRedisLastError()))));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&eCommand Error: &c" + TimeUtils.formatIntoCalendarString(new Date(qLib.getInstance().getLocalRedisLastError()))));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&5&m---------------------------------------------"));
    }

}
