package net.lugami.practice.commands;

import com.cheatbreaker.api.CheatBreakerAPI;
import net.lugami.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import net.lugami.practice.Practice;
import net.lugami.practice.util.VisibilityUtils;

public final class SilentCommand {

    @Command(names = {"silent"}, permission = "basic.staff")
    public static void silent(Player sender) {
        if (sender.hasMetadata("ModMode")) {
            sender.removeMetadata("ModMode", Practice.getInstance());
            sender.removeMetadata("invisible", Practice.getInstance());
            if (Bukkit.getPluginManager().getPlugin("CheatBreakerAPI") != null) {
                CheatBreakerAPI.getInstance().disableAllStaffModules(sender);
            }
            sender.sendMessage(ChatColor.RED + "Silent mode disabled.");
        } else {
            sender.setMetadata("ModMode", new FixedMetadataValue(Practice.getInstance(), true));
            sender.setMetadata("invisible", new FixedMetadataValue(Practice.getInstance(), true));

            if (Bukkit.getPluginManager().getPlugin("CheatBreakerAPI") != null) {
                CheatBreakerAPI.getInstance().giveAllStaffModules(sender);
            }

            sender.sendMessage(ChatColor.GREEN + "Silent mode enabled.");
        }

        VisibilityUtils.updateVisibility(sender);
    }

}