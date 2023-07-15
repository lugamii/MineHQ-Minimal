package net.lugami.practice.party.commands;

import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.qlib.command.Command;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.chat.modes.PartyChatMode;
import net.lugami.practice.party.Party;

import org.bukkit.entity.Player;

public final class PartyLeaveCommand {

    @Command(names = {"party leave", "p leave", "t leave", "team leave", "leave", "f leave"}, permission = "")
    public static void partyLeave(Player sender) {
        Party party = Practice.getInstance().getPartyHandler().getParty(sender);

        if (party == null) {
            sender.sendMessage(PracticeLang.NOT_IN_PARTY);
        } else {
            ChatPlayer chatPlayer = ChatHandler.getChatPlayer(sender.getUniqueId());
                chatPlayer.removeProvider(new PartyChatMode());
            party.leave(sender);
        }
    }

}