package net.lugami.practice.match.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import net.lugami.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ToggleMatchCommands {

    @Command(names = { "toggleMatches unranked" }, permission = "op")
    public static void toggleMatchesUnranked(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isUnrankedMatchesDisabled();
        matchHandler.setUnrankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Unranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

    @Command(names = { "toggleMatches ranked" }, permission = "op")
    public static void toggleMatchesRanked(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        boolean newState = !matchHandler.isRankedMatchesDisabled();
        matchHandler.setRankedMatchesDisabled(newState);

        sender.sendMessage(ChatColor.YELLOW + "Ranked matches are now " + ChatColor.UNDERLINE + (newState ? "disabled" : "enabled") + ChatColor.YELLOW + ".");
    }

}