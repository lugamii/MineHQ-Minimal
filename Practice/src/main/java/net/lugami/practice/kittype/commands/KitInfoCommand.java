package net.lugami.practice.kittype.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitInfoCommand {

    @Command(names = { "kittype info" }, permission = "op", description = "Creates a new kit-type")
    public static void execute(Player player, @Param(name = "name")KitType kitType) {

        player.sendMessage(ChatColor.RED + "Name:  " + ChatColor.GRAY + kitType.getColoredDisplayName());
        player.sendMessage(ChatColor.RED + "KB Profile: " + ChatColor.GRAY + kitType.getKnockbackName());

    }

}
