package net.lugami.practice.follow.listener;

import net.lugami.practice.setting.event.SettingUpdateEvent;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.match.event.MatchCountdownStartEvent;
import net.lugami.practice.match.event.MatchSpectatorLeaveEvent;
import net.lugami.practice.setting.Setting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class FollowGeneralListener implements Listener {

    private final FollowHandler followHandler;

    public FollowGeneralListener(FollowHandler followHandler) {
        this.followHandler = followHandler;
    }

    @EventHandler
    public void onMatchStart(MatchCountdownStartEvent event) {
        Match match = event.getMatch();

        for (MatchTeam team : match.getTeams()) {
            for (UUID member : team.getAllMembers()) {
                Player memberBukkit = Bukkit.getPlayer(member);

                for (UUID follower : followHandler.getFollowers(memberBukkit)) {
                    match.addSpectator(Bukkit.getPlayer(follower), memberBukkit);
                }
            }
        }
    }
    @EventHandler
    public void onMatchSpectatorLeave(MatchSpectatorLeaveEvent event) {
        // leaving while spectating a match counts as typing /unfollow
        followHandler.stopFollowing(event.getSpectator());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // can't follow an offline player
        for (UUID follower : followHandler.getFollowers(event.getPlayer())) {
            followHandler.stopFollowing(Bukkit.getPlayer(follower));
        }

        // garbage collects players who leave
        followHandler.stopFollowing(event.getPlayer());
    }

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent event) {
        if (event.getSetting() != Setting.ALLOW_SPECTATORS || event.isEnabled()) {
            return;
        }

        // can't follow a player who doesn't allow spectators
        for (UUID follower : followHandler.getFollowers(event.getPlayer())) {
            Player followerPlayer = Bukkit.getPlayer(follower);

            if (followerPlayer.isOp() || followerPlayer.hasMetadata("ModMode")) {
                continue;
            }

            followHandler.stopFollowing(followerPlayer);
        }
    }

}