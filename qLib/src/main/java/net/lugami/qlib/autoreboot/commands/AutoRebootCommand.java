package net.lugami.qlib.autoreboot.commands;

import java.util.concurrent.TimeUnit;

import net.lugami.qlib.qLib;
import net.lugami.qlib.autoreboot.AutoRebootHandler;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AutoRebootCommand {

    @Command(names={"reboot"}, permission="server.reboot")
    public static void reboot(CommandSender sender, @Param(name="time") String unparsedTime) {
        try {
            unparsedTime = unparsedTime.toLowerCase();
            int time = TimeUtils.parseTime(unparsedTime);
            AutoRebootHandler.rebootServer(time, TimeUnit.SECONDS);
            sender.sendMessage(ChatColor.YELLOW + "Started auto reboot.");
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
    }

    @Command(names={"reboot cancel"}, permission="server.reboot")
    public static void rebootCancel(CommandSender sender) {
        if (!AutoRebootHandler.isRebooting()) {
            sender.sendMessage(ChatColor.RED + "No reboot has been scheduled.");
        } else {
            AutoRebootHandler.cancelReboot();
            qLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " \u26a0");
            qLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "The server reboot has been cancelled.");
            qLib.getInstance().getServer().broadcastMessage(ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " \u26a0");
        }
    }

}

