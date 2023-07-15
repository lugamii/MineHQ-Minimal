package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SuicideCommand {

    @Command(names={"suicide"}, permission="basic.suicide", description = "Take your own life")
    public static void suicide(Player sender) {
        sender.setHealth(0.0);
        sender.sendMessage(ChatColor.GOLD + "You have been killed.");
    }
}

