package net.lugami.bridge.bukkit.commands.user;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.global.profile.Profile;

public class UserSetSuffixCommand {

    @Command(names = {"user setsuffix"}, permission = "bridge.user", description = "Set a players suffix", async = true, hidden = true)
    public static void UserSetSuffixCmd(CommandSender s, @Param(name = "player") Profile pf, @Param(name = "suffix", wildcard = true) String suffix) {
        String tag = ChatColor.translateAlternateColorCodes('&', suffix);
        if (suffix.equals("clear")) tag = "";
        pf.setSuffix(tag);
        pf.saveProfile();
        s.sendMessage("§aSuccessfully " + (tag.equals("") ? "cleared" : "set") + " the suffix of " + pf.getUsername() + (!tag.equals("") ? " to " + tag : ""));
    }
}
