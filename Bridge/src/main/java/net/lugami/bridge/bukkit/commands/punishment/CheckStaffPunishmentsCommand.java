package net.lugami.bridge.bukkit.commands.punishment;

import net.lugami.bridge.bukkit.commands.punishment.menu.staffhistory.MainStaffPunishmentListMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.global.profile.Profile;
import org.bukkit.entity.Player;

public class CheckStaffPunishmentsCommand {

    @Command(names = {"staffpunishments", "checkstaffpunishments", "staffhistory", "staffhist"}, permission = "bridge.staffhistory", description = "Check a player's active punishments", async = true)
    public static void staffPunishments(Player sender, @Param(name = "target") Profile target){
        new MainStaffPunishmentListMenu(target.getUuid().toString(), target.getUsername()).openMenu(sender);
    }
}