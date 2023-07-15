package net.lugami.practice.commands;

import com.google.common.collect.ImmutableList;
import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.match.MatchHandler;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Generic /help commands, changes message sent based on if sender is playing in
 * or spectating a match.
 */
public final class HelpCommand {

    private static final List<String> HELP_MESSAGE_HEADER = ImmutableList.of(
            Practice.getInstance().getDominantColor() + PracticeLang.LONG_LINE,
            "§9§lPractice Help",
            Practice.getInstance().getDominantColor() + PracticeLang.LONG_LINE,
            "§6§lRemember: §eMost things are clickable!",
            ""
    );

    private static final List<String> HELP_MESSAGE_player = ImmutableList.of(
            "§9Common Commands:",
            "§e/duel <player> §7- Challenge a player to a duel",
            "§e/party invite <player> §7- Invite a player to a party",
            "",
            "§9Other Commands:",
            "§e/party help §7- Information on party commands",
            "§e/report <player> <reason> §7- Report a player for violating the rules",
            "§e/request <message> §7- Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_MATCH = ImmutableList.of(
            "§9Common Commands:",
            "§e/spectate <player> §7- Spectate a player in a match",
            "§e/report <player> <reason> §7- Report a player for violating the rules",
            "§e/request <message> §7- Request assistance from a staff member"
    );

    private static final List<String> HELP_MESSAGE_FOOTER = ImmutableList.of(
            "",
            "§9Server Information:",
            "§eOfficial Teamspeak §7- §dts." + Practice.getInstance().getNetworkWebsite(),
            "§eOfficial Rules §7- §dwww." + Practice.getInstance().getNetworkWebsite() + "/rules",
            "§eStore §7- §dstore." + Practice.getInstance().getNetworkWebsite(),
            "§ePractice Leaderboards §7- §dwww." + Practice.getInstance().getNetworkWebsite() + "/stats/practice",
            Practice.getInstance().getDominantColor() + PracticeLang.LONG_LINE
    );

    @Command(names = {"help", "?", "halp", "helpme"}, permission = "")
    public static void help(Player sender) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        HELP_MESSAGE_HEADER.forEach(sender::sendMessage);

        if (matchHandler.isPlayingOrSpectatingMatch(sender)) {
            HELP_MESSAGE_MATCH.forEach(sender::sendMessage);
        } else {
            HELP_MESSAGE_player.forEach(sender::sendMessage);
        }

        HELP_MESSAGE_FOOTER.forEach(sender::sendMessage);
    }

}