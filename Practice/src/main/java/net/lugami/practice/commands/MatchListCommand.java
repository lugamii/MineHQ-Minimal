package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MatchListCommand {

    @Command(names = {"match list"}, permission = "op")
    public static void matchList(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(sender);

            if (match != null) {
                sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------");
                sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Match List");
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.RED + match.getSimpleDescription(true));
            } else {
                sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------");
                sender.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Match List");
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.RED + ChatColor.ITALIC.toString() + "No matches found...");
            }
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------");
        }
    }