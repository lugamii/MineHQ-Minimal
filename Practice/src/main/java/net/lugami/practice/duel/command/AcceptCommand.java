package net.lugami.practice.duel.command;

import com.google.common.collect.ImmutableList;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.practice.Practice;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.duel.DuelHandler;
import net.lugami.practice.duel.DuelInvite;
import net.lugami.practice.duel.PartyDuelInvite;
import net.lugami.practice.duel.PlayerDuelInvite;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.validation.PracticeValidation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class AcceptCommand {

    @Command(names = {"accept"}, permission = "")
    public static void accept(Player sender, @Param(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't accept a duel from yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            // party accepting from party (legal)
            PartyDuelInvite invite = duelHandler.findInvite(targetParty, senderParty);

            if (invite != null) {
                acceptParty(sender, senderParty, targetParty, invite);
            } else {
                // we grab the leader's name as the member targeted might not be the leader
                Profile leaderName = BridgeGlobal.getProfileHandler().getProfileByUUID(targetParty.getLeader());
                sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + leaderName + "'s party.");
            }
        } else if (senderParty == null && targetParty == null) {
            // player accepting from player (legal)
            PlayerDuelInvite invite = duelHandler.findInvite(target, sender);
            if (invite != null) {
                acceptPlayer(sender, target, invite);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
            }
        } else if (senderParty == null) {
            // player accepting from party (illegal)
            sender.sendMessage(ChatColor.RED + "You don't have a duel invite from " + target.getName() + ".");
        } else {
            // party accepting from player (illegal)
            sender.sendMessage(ChatColor.RED + "Your party doesn't have a duel invite from " + target.getName() + "'s party.");
        }
    }

    private static void acceptParty(Player sender, Party senderParty, Party targetParty, DuelInvite invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!senderParty.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
            return;
        }

        if (!PracticeValidation.canAcceptDuel(senderParty, targetParty, sender)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(senderParty.getMembers()), new MatchTeam(targetParty.getMembers())),
                invite.getKitType(),
                false,
                true // see Match#allowRematches
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            senderParty.message(PracticeLang.ERROR_WHILE_STARTING_MATCH);
            targetParty.message(PracticeLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

    private static void acceptPlayer(Player sender, Player target, DuelInvite invite) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();

        if (!PracticeValidation.canAcceptDuel(sender, target)) {
            return;
        }

        Match match = matchHandler.startMatch(
                ImmutableList.of(new MatchTeam(sender.getUniqueId()), new MatchTeam(target.getUniqueId())),
                invite.getKitType(),
                false,
                true // see Match#allowRematches
        );

        if (match != null) {
            // only remove invite if successful
            duelHandler.removeInvite(invite);
        } else {
            sender.sendMessage(PracticeLang.ERROR_WHILE_STARTING_MATCH);
            target.sendMessage(PracticeLang.ERROR_WHILE_STARTING_MATCH);
        }
    }

}