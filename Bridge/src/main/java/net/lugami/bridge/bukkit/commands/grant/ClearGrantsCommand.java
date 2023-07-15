package net.lugami.bridge.bukkit.commands.grant;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.global.profile.Profile;

public class ClearGrantsCommand {

    @Command(names = {"cleargrants"}, permission = "bridge.cleargrants", description = "Clear player's grants from the entire network", async = true)
    public static void clearGrants(CommandSender s, @Param(name = "target") Profile target) {

        if (target.getGrants().isEmpty()) {
            s.sendMessage(ChatColor.RED + target.getUsername() + " does not have any grants.");
            return;
        }
        s.sendMessage(ChatColor.GREEN + "Successfully cleared " + target.getCurrentGrant().getRank().getColor() + target.getUsername() + ChatColor.GREEN + "'s grants.");
        target.getGrants().clear();
        target.saveProfile();
    }
}

