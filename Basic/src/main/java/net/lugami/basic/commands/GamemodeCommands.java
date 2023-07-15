package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommands {

    @Command(names={"gamemode", "gm"}, permission="basic.gamemode", description = "Set a player's gamemode")
    public static void gamemode(CommandSender sender, @Param(name="mode", defaultValue="-0*toggle*0-") GameMode mode, @Param(name="player", defaultValue="self") Player target) {
        GamemodeCommands.run(sender, target, mode);
    }

    @Command(names={"gms", "gm0"}, permission="basic.gamemode", description = "Set a player's gamemode to survival")
    public static void gms(CommandSender sender, @Param(name="player", defaultValue="self") Player target) {
        GamemodeCommands.run(sender, target, GameMode.SURVIVAL);
    }

    @Command(names={"gmc", "gm1"}, permission="basic.gamemode", description = "Set a player's gamemode to creative")
    public static void gmc(CommandSender sender, @Param(name="player", defaultValue="self") Player target) {
        GamemodeCommands.run(sender, target, GameMode.CREATIVE);
    }

    @Command(names={"gma", "gm2"}, permission="basic.gamemode", description = "Set a player's gamemode to adventure")
    public static void gma(CommandSender sender, @Param(name="player", defaultValue="self") Player target) {
        GamemodeCommands.run(sender, target, GameMode.ADVENTURE);
    }

    private static void run(CommandSender sender, Player target, GameMode mode) {
        if (!sender.equals(target) && !sender.hasPermission("basic.gamemode.other")) {
            sender.sendMessage(ChatColor.RED + "No permission to set other player's gamemode.");
            return;
        }
        target.setGameMode(mode);
        if (!sender.equals(target)) {
            sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + " is now in " + ChatColor.WHITE + mode + ChatColor.GOLD + " mode.");
        }
        target.sendMessage(ChatColor.GOLD + "You are now in " + ChatColor.WHITE + mode + ChatColor.GOLD + " mode.");
    }
}

