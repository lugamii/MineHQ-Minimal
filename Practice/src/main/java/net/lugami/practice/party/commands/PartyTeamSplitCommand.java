package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.party.PartyUtils;

import org.bukkit.entity.Player;

public final class PartyTeamSplitCommand {

    @Command(names = {"party teamsplit", "p teamsplit", "t teamsplit", "team teamsplit", "f teamsplit"}, permission = "")
    public static void partyTeamSplit(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            PartyUtils.startTeamSplit(party, sender);
        }
    }

}