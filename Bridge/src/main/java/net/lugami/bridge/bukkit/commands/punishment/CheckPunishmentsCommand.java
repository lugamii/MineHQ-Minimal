package net.lugami.bridge.bukkit.commands.punishment;

import net.lugami.bridge.bukkit.commands.punishment.menu.MainPunishmentMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Flag;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.global.profile.Profile;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CheckPunishmentsCommand {

    @Command(names = {"checkpunishments", "cp", "c", "history"}, permission = "bridge.checkpunishments", description = "Check a player's active punishments", async = true)
    public static void checkPunishments(Player sender, @Flag(value = {"gui", "menu"}, description = "Check a player's active punishments ingame") boolean gui, @Param(name = "target", extraData = "get") Profile target) {
        if (!gui) {
            FancyMessage message = new FancyMessage(ChatColor.GREEN + "[Click Here]" + ChatColor.YELLOW + " to view all of " + target.getUsername() + "'s punishments");
            message.tooltip(ChatColor.GRAY + "Click here: https://www.bridge.rip/u/" + target.getUsername() + "/punishments").link("https://www.bridge.rip/u/" + target.getUsername() + "/punishments");
            message.send(sender);
        } else {
            new MainPunishmentMenu(target.getUuid().toString(), target.getUsername()).openMenu(sender);
        }

    }
}
