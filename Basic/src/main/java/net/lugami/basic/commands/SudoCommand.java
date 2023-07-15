package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand {

    @Command(names={"sudo"}, permission="basic.sudo", description = "Force a player to perform a commands")
    public static void sudo(CommandSender sender, @Param(name="player") Player target, @Param(name="commands", wildcard=true) String command) {
        target.chat("/" + command);
        sender.sendMessage(ChatColor.GOLD + "Forced " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + " to run " + ChatColor.WHITE + "'/" + command + "'" + ChatColor.GOLD + ".");
    }
}

