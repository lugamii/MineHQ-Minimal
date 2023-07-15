package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyAccessRestriction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyOpenCommand {

    @Command(names = {"party open", "p open", "t open", "team open", "f open", "party unlock", "p unlock", "t unlock", "team unlock", "f unlock"}, permission = "")
    public static void partyOpen(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.PUBLIC) {
            sender.sendMessage(ChatColor.RED + "Your party is already open.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PUBLIC);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.GREEN + "open" + ChatColor.YELLOW + ".");
        }
    }

}
