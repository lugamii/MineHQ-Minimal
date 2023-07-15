package net.lugami.practice.tournament.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TournamentCancelCommand {

    @Command(names = { "tournament cancel", "tcancel", "tourn cancel"},  permission = "practice.admin")
    public static void tournamentCancel(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cTournament cancelled."));
        Bukkit.broadcastMessage("");
        Practice.getInstance().getTournamentHandler().setTournament(null);
    }
}
