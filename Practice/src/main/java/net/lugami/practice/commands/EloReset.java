package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.Practice;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EloReset {

    @Command(names = "eloreset", permission = "practice.admin", description = "Manually reset the player's elo")
    public static void eloreset(Player sender, @Param(name="target") OfflinePlayer target) {
        Practice.getInstance().getEloHandler().resetElo(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Resetting elo of " + target.getName() + ChatColor.GREEN + ".");
    }
}
