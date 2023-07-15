package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class ChunksCommand {

    @Command(names={"chunks"}, permission="op")
    public static void chunks(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Loaded chunks per world:");
        for (World world : Bukkit.getWorlds()) {
            sender.sendMessage(ChatColor.YELLOW + world.getName() + ": " + ChatColor.RED + world.getLoadedChunks().length);
        }
    }
}

