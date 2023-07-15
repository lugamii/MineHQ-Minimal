package net.lugami.practice.arena.command;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ArenaFreeCommand {

    @Command(names = { "arena free" }, permission = "op")
    public static void arenaFree(Player sender) {
        Practice.getInstance().getArenaHandler().getGrid().free();
        sender.sendMessage(ChatColor.GREEN + "Arena grid has been freed.");
    }

}