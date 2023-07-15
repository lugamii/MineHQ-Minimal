package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyAccessRestriction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyPasswordCommand {

    @Command(names = {"party password", "p password", "t password", "team password", "party pass", "p pass", "t pass", "team pass", "f password", "f pass"}, permission = "")
    public static void partyPassword(Player sender, @Param(name = "password") String password) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else {
            party.setAccessRestriction(PartyAccessRestriction.PASSWORD);
            party.setPassword(password);

            sender.sendMessage(ChatColor.YELLOW + "Your party's password is now " + ChatColor.AQUA + password + ChatColor.YELLOW + ".");
        }
    }

}
