package net.lugami.practice.follow.command;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.Practice;
import net.lugami.practice.follow.FollowHandler;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import net.lugami.practice.validation.PracticeValidation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FollowCommand {

    @Command(names={"follow"}, permission= "")
    public static void follow(Player sender, @Param(name="target") Player target) {
        if (!PracticeValidation.canFollowSomeone(sender)) {
            return;
        }

        FollowHandler followHandler = Practice.getInstance().getFollowHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "No, you can't follow yourself.");
            return;
        } else if (!settingHandler.getSetting(target, Setting.ALLOW_SPECTATORS)) {
            if (sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "Bypassing " + target.getName() + "'s no spectators preference...");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " doesn't allow spectators at the moment.");
                return;
            }
        }

        followHandler.getFollowing(sender).ifPresent(fo -> UnfollowCommand.unfollow(sender));

        if (matchHandler.isSpectatingMatch(sender)) {
            matchHandler.getMatchSpectating(sender).removeSpectator(sender);
        }

        followHandler.startFollowing(sender, target);
    }

}