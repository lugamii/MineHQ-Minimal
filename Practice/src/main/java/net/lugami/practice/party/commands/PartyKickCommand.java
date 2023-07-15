package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyKickCommand {

    @Command(names = {"party kick", "p kick", "t kick", "team kick", "f kick"}, permission = "")
    public static void partyKick(Player sender, @Param(name = "player") Player target) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
        } else if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You cannot kick yourself.");
        } else if (!party.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't in your party.");
        } else {
            party.kick(target);
        }
    }

}