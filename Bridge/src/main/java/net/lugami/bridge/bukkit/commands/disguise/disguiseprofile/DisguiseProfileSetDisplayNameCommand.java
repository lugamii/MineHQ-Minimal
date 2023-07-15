package net.lugami.bridge.bukkit.commands.disguise.disguiseprofile;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguiseProfile;

public class DisguiseProfileSetDisplayNameCommand {

    @Command(names = {"disguiseprofile setdisplayname"}, permission = "bridge.disguise.admin", description = "Set a disguise profile display name", hidden = true)
    public static void disguised(Player player, @Param(name = "name") String name, @Param(name = "displayName", wildcard = true) String displayName) {
        DisguiseProfile profile = BridgeGlobal.getDisguiseManager().getProfile(name);

        profile.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        BridgeGlobal.getDisguiseManager().saveProfiles(true);
        player.sendMessage(ChatColor.GREEN + "You've changed display name of " + ChatColor.RESET + profile.getName() + ChatColor.GREEN + " to " + displayName + ChatColor.GREEN + '.');
    }
}

