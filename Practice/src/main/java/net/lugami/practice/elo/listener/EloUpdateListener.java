package net.lugami.practice.elo.listener;

import com.google.common.base.Joiner;
import net.lugami.practice.match.event.MatchEndEvent;
import net.lugami.practice.match.event.MatchTerminateEvent;
import net.lugami.practice.elo.EloCalculator;
import net.lugami.practice.elo.EloHandler;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.util.PatchedPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public final class EloUpdateListener implements Listener {

    private static final String ELO_CHANGE_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&eElo Changes: &a%s +%d (%d) &c%s -%d (%d)");

    private final EloHandler eloHandler;
    private final EloCalculator eloCalculator;

    public EloUpdateListener(EloHandler eloHandler, EloCalculator eloCalculator) {
        this.eloHandler = eloHandler;
        this.eloCalculator = eloCalculator;
    }

    // we actually save elo when the match first ends but only
    // send messages when it terminates (when players go back to
    // the player)
    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        KitType kitType = match.getKitType();
        List<MatchTeam> teams = match.getTeams();

        if (!match.isRanked() || teams.size() != 2 || match.getWinner() == null) {
            return;
        }

        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        EloCalculator.Result result = eloCalculator.calculate(
            eloHandler.getElo(winnerTeam.getAllMembers(), kitType),
            eloHandler.getElo(loserTeam.getAllMembers(), kitType)
        );

        eloHandler.setElo(winnerTeam.getAllMembers(), kitType, result.getWinnerNew());
        eloHandler.setElo(loserTeam.getAllMembers(), kitType, result.getLoserNew());

        match.setEloChange(result);
    }

    // see comment on onMatchEnd method
    @EventHandler
    public void onMatchTerminate(MatchTerminateEvent event) {
        Match match = event.getMatch();
        EloCalculator.Result result = match.getEloChange();

        if (result == null) {
            return;
        }

        List<MatchTeam> teams = match.getTeams();
        MatchTeam winnerTeam = match.getWinner();
        MatchTeam loserTeam = teams.get(0) == winnerTeam ? teams.get(1) : teams.get(0);

        String winnerStr;
        String loserStr;
        Player player = Bukkit.getPlayer((winnerTeam.getFirstMember()));
        Player player1 = Bukkit.getPlayer((loserTeam.getFirstMember()));
        if (winnerTeam.getAllMembers().size() == 1 && loserTeam.getAllMembers().size() == 1) {
            winnerStr = player.getName();
            loserStr = player1.getName();
        } else {
            winnerStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(winnerTeam.getAllMembers()));
            loserStr = Joiner.on(", ").join(PatchedPlayerUtils.mapToNames(loserTeam.getAllMembers()));
        }
        match.messageAll(String.format(ELO_CHANGE_MESSAGE, winnerStr, result.getWinnerGain(), result.getWinnerNew(), loserStr, -result.getLoserGain(), result.getLoserNew()));
    }

}