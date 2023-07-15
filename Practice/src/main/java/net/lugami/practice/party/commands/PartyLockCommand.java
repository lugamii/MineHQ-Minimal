package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyAccessRestriction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyLockCommand {

    @Command(names = {"party lock", "p lock", "t lock", "team lock", "f lock"}, permission = "")
    public static void partyLock(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else if (party.getAccessRestriction() == PartyAccessRestriction.INVITE_ONLY) {
            sender.sendMessage(ChatColor.RED + "Your party is already locked.");
        } else {
            party.setAccessRestriction(PartyAccessRestriction.INVITE_ONLY);
            sender.sendMessage(ChatColor.YELLOW + "Your party is now " + ChatColor.RED + "locked" + ChatColor.YELLOW + ".");
        }
    }

}
