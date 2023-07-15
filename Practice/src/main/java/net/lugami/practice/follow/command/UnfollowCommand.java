package net.lugami.practice.follow.command;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class UnfollowCommand {

    @Command(names={"unfollow"}, permission = "")
    public static void unfollow(Player sender) {
        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (!followHandler.getFollowing(sender).isPresent()) {
            sender.sendMessage(ChatColor.RED + "You're not following anybody.");
            return;
        }

        Match spectating = matchHandler.getMatchSpectating(sender);

        if (spectating != null) {
            spectating.removeSpectator(sender);
        }

        followHandler.stopFollowing(sender);
    }

}