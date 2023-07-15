package net.lugami.bridge.bukkit.commands.punishment;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.global.profile.Profile;
import org.bukkit.ChatColor;

public class ClearPunishmentsCommand {

    @Command(names = {"clearpunishments", "clearhistory"}, permission = "bridge.clearpunishments", description = "Clear player's punishments from the entire network", async = true)
    public static void clearPunishments(CommandSender s, @Param(name = "target") Profile target) {

        if (target.getPunishments().isEmpty()) {
            s.sendMessage(ChatColor.RED + target.getUsername() + " does not have any punishments.");
            return;
        }
        s.sendMessage(ChatColor.GREEN + "Successfully cleared " + target.getCurrentGrant().getRank().getColor() + target.getUsername() + ChatColor.GREEN + "'s punishments.");
        target.getPunishments().clear();
        target.saveProfile();
    }
}

