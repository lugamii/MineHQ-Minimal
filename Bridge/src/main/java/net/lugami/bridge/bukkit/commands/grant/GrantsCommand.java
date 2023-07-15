package net.lugami.bridge.bukkit.commands.grant;

import mkremins.fanciful.FancyMessage;
import net.lugami.bridge.bukkit.commands.grant.menu.grants.GrantsMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Flag;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.profile.Profile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GrantsCommand {

    @Command(names = "grants", permission = "bridge.grants", description = "Check a player's grants", async = true)
    public static void grantsCmd(Player sender, @Flag(value = {"w", "website"}, description = "Check a player's active grants through the website") boolean website, @Param(name = "player", extraData = "get") Profile profile) {

        if (BukkitAPI.getPlayerRank(profile, true).getPriority() > BukkitAPI.getPlayerRank(sender, true).getPriority()) {

            sender.sendMessage(ChatColor.RED + "You cannot view the grants of \"" + (profile.getDisguise() != null ? profile.getDisguise().getDisguiseName() : profile.getUsername()) + "\".");
            return;
        }

        if (website) {
            FancyMessage message = new FancyMessage(org.bukkit.ChatColor.GREEN + "[Click Here]" + org.bukkit.ChatColor.YELLOW + " to view all of " + profile.getUsername() + "'s grants");
            message.tooltip(org.bukkit.ChatColor.GRAY + "Click here: https://www.bridge.rip/u/" + profile.getUsername() + "/grants").link("https://www.bridge.rip/u/" + profile.getUsername() + "/grants");
            message.send(sender);
        } else {
            List<Grant> allGrants = profile.getGrants().stream().filter(grant -> !grant.getRank().isDefaultRank()).sorted((first, second) -> {
                if (first.getInitialTime() > second.getInitialTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }).collect(Collectors.toList());
            new GrantsMenu(profile, allGrants).openMenu(sender);
        }
    }
}
