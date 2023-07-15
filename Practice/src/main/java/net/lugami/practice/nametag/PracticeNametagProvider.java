package net.lugami.practice.nametag;
;
import net.lugami.practice.pvpclasses.pvpclasses.ArcherClass;
import net.lugami.qlib.nametag.NametagInfo;
import net.lugami.qlib.nametag.NametagProvider;
import net.lugami.practice.Practice;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchTeam;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PracticeNametagProvider extends NametagProvider {

    public PracticeNametagProvider() {
        super("Practice Provider", 5);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {

        ChatColor prefixColor = getNameColor(toRefresh, refreshFor);
        return createNametag(prefixColor.toString(), "");
    }

    public static ChatColor getNameColor(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(toRefresh)) {
            return getNameColorMatch(toRefresh, refreshFor);
        } else {
            return getNameColorplayer(toRefresh, refreshFor);
        }
    }

    private static ChatColor getNameColorMatch(Player toRefresh, Player refreshFor) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        Match toRefreshMatch = matchHandler.getMatchPlayingOrSpectating(toRefresh);
        MatchTeam toRefreshTeam = toRefreshMatch.getTeam(toRefresh.getUniqueId());

        // they're a spectator, so we see them as gray
        if (toRefreshTeam == null) {
            return ChatColor.GRAY;
        }

        MatchTeam refreshForTeam = toRefreshMatch.getTeam(refreshFor.getUniqueId());

        // if we can't find a current team, check if they have any
        // previously teams we can use for this
        if (refreshForTeam == null) {
            refreshForTeam = toRefreshMatch.getPreviousTeam(refreshFor.getUniqueId());
        }

        // if we were/are both on teams display a friendly/enemy color
        if (refreshForTeam != null) {
            if (toRefreshTeam == refreshForTeam) {
                return ChatColor.GREEN;
            } else {
                if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && System.currentTimeMillis() < ArcherClass.getMarkedPlayers().get(toRefresh.getName())) {

                    return ChatColor.YELLOW;
                }
                return ChatColor.RED;
            }
        }

        // if we're a spectator just display standard colors
        List<MatchTeam> teams = toRefreshMatch.getTeams();

        // we have predefined colors for 'normal' matches
        if (teams.size() == 2) {
            // team 1 = LIGHT_PURPLE, team 2 = AQUA
            if (toRefreshTeam == teams.get(0)) {
                return ChatColor.LIGHT_PURPLE;
            } else {
                return ChatColor.AQUA;
            }
        } else {
            // we don't have colors defined for larger matches
            // everyone is just red for spectators
            return ChatColor.RED;
        }
    }

    private static ChatColor getNameColorplayer(Player toRefresh, Player refreshFor) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();

        Optional<UUID> following = followHandler.getFollowing(refreshFor);
        boolean refreshForFollowingTarget = following.isPresent() && following.get().equals(toRefresh.getName());

        if (refreshForFollowingTarget) {
            return ChatColor.AQUA;
        } else {
            return ChatColor.GREEN;
        }
    }

}