package net.lugami.practice.party.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import net.lugami.practice.party.PartyHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class PartyCreateCommand {

    @Command(names = {"party create", "p create", "t create", "team create", "f create"}, permission = "")
    public static void partyCreate(Player sender) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();

        if (partyHandler.hasParty(sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a party.");
            return;
        }

        partyHandler.getOrCreateParty(sender);
        sender.sendMessage(ChatColor.YELLOW + "Created a new party.");
    }

}