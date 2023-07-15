package net.lugami.bridge.bukkit.commands.disguise.disguiseprofile;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.disguise.DisguiseProfile;

public class DisguiseProfileInfoCommand {

    @Command(names = {"disguiseprofile info"}, permission = "bridge.disguise.admin", description = "Get information about a disguise profile", hidden = true)
    public static void disguiseprofileinfo(CommandSender s, @Param(name = "name") DisguiseProfile disguiseProfile) {
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage(disguiseProfile.getDisplayName() + " Disguise Profile §7❘ §fInformation");
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage("§6Name: §f" + disguiseProfile.getName() + " §7(" + net.md_5.bungee.api.ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")");
        s.sendMessage("§6Display Name: §f" + disguiseProfile.getDisplayName() + " §7(" + net.md_5.bungee.api.ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")");
        s.sendMessage("§6Skin: §f" + disguiseProfile.getSkinName());
        s.sendMessage("");
    }
}
