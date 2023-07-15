package net.lugami.bridge.bukkit.commands.punishment;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.global.profile.Profile;

public class ClearStaffPunishmentsCommand {

    @Command(names = {"clearstaffpunishments", "clearstaffhistory"}, permission = "bridge.clearstaffpunishments", description = "Clear staff member's punishments from the entire network", async = true)
    public static void clearPunishments(CommandSender sender, @Param(name = "target") Profile target) {

        if (!target.getCurrentGrant().getRank().isStaff() && target.getRemovedStaffOn() == 0) {
            sender.sendMessage(ChatColor.RED + "That player has never been apart of the staff team.");
            return;
        }

        if (target.getStaffPunishments().isEmpty()) {
            sender.sendMessage(ChatColor.RED + target.getUsername() + " does not have any staff punishments.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Successfully cleared " + target.getCurrentGrant().getRank().getColor() + target.getUsername() + ChatColor.GREEN + "'s Staff History.");
        target.getStaffPunishments().clear();
        target.saveProfile();
    }
}

