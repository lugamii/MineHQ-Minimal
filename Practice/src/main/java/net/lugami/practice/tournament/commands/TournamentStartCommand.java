package net.lugami.practice.tournament.commands;

import net.lugami.practice.tournament.menu.TournamentMenu;
import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TournamentStartCommand {

    @Command(names = { "tournament start", "tourn start"}, permission = "practice.tournament.start")
    public static void host(Player sender) {
        new TournamentMenu(kitType -> kitType.getColoredDisplayName(), ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a tournament kit").openMenu(sender);
        }
    }
