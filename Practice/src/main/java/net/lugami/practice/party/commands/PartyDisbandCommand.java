package net.lugami.practice.party.commands;

import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.chat.modes.PartyChatMode;
import net.lugami.practice.party.Party;

import org.bukkit.entity.Player;

public final class PartyDisbandCommand {

    @Command(names = {"party disband", "p disband", "t disband", "team disband", "f disband"}, permission = "")
    public static void partyDisband(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
            return;
        }

        if (!party.isLeader(sender.getUniqueId())) {
            sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
            return;
        }

        ChatPlayer chatPlayer = ChatHandler.getChatPlayer(sender.getUniqueId());
        chatPlayer.removeProvider(new PartyChatMode());
        party.disband();
    }

}