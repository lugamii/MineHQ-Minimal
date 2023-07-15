package net.lugami.practice.elo.command;

import net.lugami.practice.lobby.menu.StatisticsMenu;
import net.lugami.qlib.command.Command;
import org.bukkit.entity.Player;

public class LeaderboardsCommand {

    @Command(names = {"leaderboards"}, permission = "")
    public static void leaderboards(Player sender) {

        new StatisticsMenu().openMenu(sender);
    }

}
