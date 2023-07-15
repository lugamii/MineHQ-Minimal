package net.lugami.practice.elo.command;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.Practice;
import net.lugami.practice.elo.EloHandler;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class EloSetCommands {

    @Command(names = {"elo setSolo"}, permission = "op")
    public static void eloSetSolo(Player sender, @Param(name="target") Player target, @Param(name="kit type") KitType kitType, @Param(name="new elo") int newElo) {
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        eloHandler.setElo(target, kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + target.getName() + "'s " + kitType.getDisplayName() + " elo to " + newElo + ".");
    }

    @Command(names = {"elo setTeam"}, permission = "op")
    public static void eloSetTeam(Player sender, @Param(name="target") Player target, @Param(name="kit type") KitType kitType, @Param(name="new elo") int newElo) {
        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        Party targetParty = partyHandler.getParty(target);

        if (targetParty == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not in a party.");
            return;
        }

        eloHandler.setElo(targetParty.getMembers(), kitType, newElo);
        sender.sendMessage(ChatColor.YELLOW + "Set " + kitType.getDisplayName() + " elo of " + UUIDUtils.name(targetParty.getLeader()) + "'s party to " + newElo + ".");
    }

}