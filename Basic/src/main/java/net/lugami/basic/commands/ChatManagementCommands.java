package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManagementCommands {

    @Command(names={"clearchat", "cc"}, permission="basic.staff", description = "Clear the chat for normal users")
    public static void clearchat(CommandSender sender) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Basic.getInstance(), () -> {
            AtomicInteger i = new AtomicInteger();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.hasPermission("basic.staff")) {
                    i.set(0);
                    while (i.get() < 1000) {
                        player.sendMessage("");
                        i.incrementAndGet();
                    }
                }
                if (player.hasPermission("basic.staff")) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
                }
                if (!player.hasPermission("basic.staff")) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by a staff member.");
                }
            });
        });
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
    }

    @Command(names={"clearchatall", "ccall", "cca"}, permission="basic.clearchatall", description = "Clear the chat for all users")
    public static void clearchatall(CommandSender sender) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(Basic.getInstance(), () -> {
            AtomicInteger i = new AtomicInteger();
            Bukkit.getOnlinePlayers().forEach(player -> {
                i.set(0);
                while (i.get() < 1000) {
                    player.sendMessage("");
                    i.incrementAndGet();
                }
                if (player.hasPermission("basic.staff")) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
                }
                if (!player.hasPermission("basic.staff")) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by a staff member.");
                }
            });
        });
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "The chat has been cleared by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + " (all).");
    }

    @Command(names={"mutechat", "rc", "restrictchat"}, permission="basic.staff", description = "Toggle chat restriction")
    public static void mutechat(CommandSender sender) {
        Basic.getInstance().getChatManager().setMuted(!Basic.getInstance().getChatManager().isMuted());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("basic.staff")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isMuted() ? "" : "un") + "muted by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
                continue;
            }
            if (!Basic.getInstance().getChatManager().isMuted()) continue;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isMuted() ? "" : "un") + "muted.");
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isMuted() ? "" : "un") + "muted by " + sender.getName() + ".");
    }

    @Command(names={"slowchat", "slow"}, permission="basic.staff", description = "Toggle chat slow status")
    public static void slow(CommandSender sender) {
        Basic.getInstance().getChatManager().setSlowed(!Basic.getInstance().getChatManager().isSlowed());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("basic.staff")) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isSlowed() ? "" : "un") + "slowed by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
                continue;
            }
            if (!Basic.getInstance().getChatManager().isSlowed()) continue;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isSlowed() ? "" : "un") + "slowed.");
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Public chat has been " + (Basic.getInstance().getChatManager().isSlowed() ? "" : "un") + "slowed by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.LIGHT_PURPLE + ".");
    }
}

