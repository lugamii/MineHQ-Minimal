package net.lugami.practice.tournament.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.practice.Practice;
import net.lugami.practice.tournament.Tournament;
import net.lugami.practice.tournament.TournamentHandler;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TournamentStatusCommand {

    private static TournamentHandler instance;

    @Command(names = { "tournament status", "tstatus", "status", "tourn status" }, permission = "")
    public static void tournamentStatus(CommandSender sender) {
        if (Practice.getInstance().getTournamentHandler().getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no active tournament.");
            return;
        }
        if (Practice.getInstance().getTournamentHandler().getTournament().getStage() == Tournament.TournamentStage.WAITING_FOR_TEAMS) {
            sender.sendMessage(ChatColor.GOLD + "Status: " + ChatColor.GRAY + "Waiting for players");
            sender.sendMessage(ChatColor.GOLD + "Players Needed: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getActiveParties().size() + "/" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartiesToStart());
            sender.sendMessage(ChatColor.GOLD + "Kit Type: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getType().getDisplayName());
            sender.sendMessage(ChatColor.GOLD + "Team Size: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize() + "v" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize());
        } else if (Practice.getInstance().getTournamentHandler().getTournament().getStage() == Tournament.TournamentStage.COUNTDOWN) {
            sender.sendMessage(ChatColor.GOLD + "Status: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getBeginNextRoundIn());
            sender.sendMessage(ChatColor.GOLD + "Players Needed: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getActiveParties().size() + "/" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartiesToStart());
            sender.sendMessage(ChatColor.GOLD + "Kit Type: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getType().getDisplayName());
            sender.sendMessage(ChatColor.GOLD + "Team Size: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize() + "v" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize());
        } else if (Practice.getInstance().getTournamentHandler().getTournament().getStage() == Tournament.TournamentStage.IN_PROGRESS) {
            sender.sendMessage(ChatColor.GOLD + "Duration: " + ChatColor.GRAY + TimeUtils.formatIntoMMSS((int) (System.currentTimeMillis() - Practice.getInstance().getTournamentHandler().getTournament().getRoundStartedAt()) / 1000));
            sender.sendMessage(ChatColor.GOLD + "Round: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getCurrentRound());
            FancyMessage message;
            message = new FancyMessage(ChatColor.GOLD + "Players: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getActiveParties().size() + "/" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartiesToStart() + ChatColor.GRAY + ChatColor.ITALIC + "[Click here]");
            message.command("/tournament list");
            message.tooltip(ChatColor.GRAY + "Click here to view the full list of players");
            sender.sendMessage(ChatColor.GOLD + "Kit Type: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getType().getDisplayName());
            sender.sendMessage(ChatColor.GOLD + "Team Size: " + ChatColor.GRAY + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize() + "v" + Practice.getInstance().getTournamentHandler().getTournament().getRequiredPartySize());
        }
//        sender.sendMessage(PracticeLang.LONG_LINE);
//        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Live" + Practice.getInstance().getDominantColor() + " Tournament &7Fights"));
//        sender.sendMessage("");
//        List<Match> ongoingMatches = Practice.getInstance().getTournamentHandler().getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED).collect(Collectors.toList());
//
//        for (Match match : ongoingMatches) {
//            MatchTeam firstTeam = match.getTeams().get(0);
//            MatchTeam secondTeam = match.getTeams().get(1);
//
//            if (firstTeam.getAllMembers().size() == 1) {
//                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.GOLD + UUIDUtils.name(firstTeam.getFirstMember()) + ChatColor.GRAY + " vs " + ChatColor.GOLD + UUIDUtils.name(secondTeam.getFirstMember()));
//            } else {
//                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.GOLD + UUIDUtils.name(firstTeam.getFirstMember()) + ChatColor.GRAY + "'s team vs " + ChatColor.GOLD + UUIDUtils.name(secondTeam.getFirstMember()) + ChatColor.GRAY + "'s team");
//            }
//        }
//        sender.sendMessage(PracticeLang.LONG_LINE);
    }
}
