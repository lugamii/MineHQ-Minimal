package net.lugami.practice.postmatchinv.commands;

import net.lugami.practice.postmatchinv.menu.PostMatchMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.Practice;
import net.lugami.practice.postmatchinv.PostMatchInvHandler;
import net.lugami.practice.postmatchinv.PostMatchPlayer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class CheckPostMatchInvCommand {

    @Command(names = { "checkPostMatchInv", "_" }, permission = "")
    public static void checkPostMatchInv(Player sender, @Param(name = "target") Player target) {
        PostMatchInvHandler postMatchInvHandler = Practice.getInstance().getPostMatchInvHandler();
        Map<UUID, PostMatchPlayer> players = postMatchInvHandler.getPostMatchData(sender.getUniqueId());

        if (players.containsKey(target.getUniqueId())) {
            new PostMatchMenu(players.get(target.getUniqueId())).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Data for " + UUIDUtils.name(target.getUniqueId()) + " not found.");
        }
    }

}