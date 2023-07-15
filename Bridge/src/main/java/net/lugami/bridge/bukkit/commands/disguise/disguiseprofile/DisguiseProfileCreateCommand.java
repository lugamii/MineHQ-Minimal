package net.lugami.bridge.bukkit.commands.disguise.disguiseprofile;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguiseProfile;

import java.util.regex.Pattern;

public class DisguiseProfileCreateCommand {

    @Command(names = {"disguiseprofile create"}, permission = "bridge.disguise.admin", description = "Create a disguise profile", hidden = true)
    public static void disguise(Player player, @Param(name = "name") String name, @Param(name = "skin") String skin) {
        if (!Pattern.compile("^\\w{1,16}$").matcher(name).matches()) {
            player.sendMessage(ChatColor.RED + "That is not a valid username.");
            return;
        }

        if (!Pattern.compile("^\\w{1,16}$").matcher(skin).matches()) {
            player.sendMessage(ChatColor.RED + "That is not a valid skin.");
            return;
        }

        boolean added = BridgeGlobal.getDisguiseManager().addProfile(name, skin);

        if (!added) {
            player.sendMessage(ChatColor.RED + "Failed to add disguise profile with name " + name + " and skin " + skin + '.');
            return;
        }

        DisguiseProfile profile = BridgeGlobal.getDisguiseManager().getProfile(name);

        profile.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        player.sendMessage(ChatColor.GREEN + "You have created a disguise profile with name " + ChatColor.RESET + name + ChatColor.GREEN + " and skin " + ChatColor.RESET + skin + ChatColor.GREEN + '.');
    }
}
