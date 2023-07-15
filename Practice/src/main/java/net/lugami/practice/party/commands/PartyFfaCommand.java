package net.lugami.practice.party.commands;

import net.lugami.practice.kittype.menu.select.SelectKitTypeMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchTeam;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.validation.PracticeValidation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PartyFfaCommand {

    @Command(names = {"party ffa", "p ffa", "t ffa", "team ffa", "f ffa"}, permission = "")
    public static void partyFfa(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (!PracticeValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PracticeValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<MatchTeam> teams = new ArrayList<>();

                for (UUID member : party.getMembers()) {
                    teams.add(new MatchTeam(member));
                }

                matchHandler.startMatch(teams, kitType, false, false);
            }, "Start a Party FFA...").openMenu(sender);
        }
    }

    @Command(names = {"party devffa", "p devffa", "t devffa", "team devffa", "f devffa"}, permission = "")
    public static void partyDevFfa(Player sender, @Param(name = "team size", defaultValue = "1") int teamSize) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        Party party = partyHandler.getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

            if (!PracticeValidation.canStartFfa(party, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                if (!PracticeValidation.canStartFfa(party, sender)) {
                    return;
                }

                List<UUID> availableMembers = new ArrayList<>(party.getMembers());
                Collections.shuffle(availableMembers);
                
                List<MatchTeam> teams = new ArrayList<>();

                while (availableMembers.size() >= teamSize) {
                    List<UUID> teamMembers = new ArrayList<>();

                    for (int i = 0; i < teamSize; i++) {
                        teamMembers.add(availableMembers.remove(0));
                    }

                    teams.add(new MatchTeam(teamMembers));
                }

                Match match = matchHandler.startMatch(teams, kitType, false, false);

                if (match != null) {
                    for (UUID leftOut : availableMembers) {
                        match.addSpectator(Bukkit.getPlayer(leftOut), null);
                    }
                }
            }, "Start Dev Party FFA...").openMenu(sender);
        }
    }

}