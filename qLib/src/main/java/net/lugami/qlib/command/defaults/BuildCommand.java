package net.lugami.qlib.command.defaults;

import net.lugami.qlib.qLib;
import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class BuildCommand {

    @Command(names={"build"}, permission="qlib.build")
    public static void build(Player sender) {
        if (sender.hasMetadata("build")) {
            sender.removeMetadata("build", qLib.getInstance());
        } else {
            sender.setMetadata("build", new FixedMetadataValue(qLib.getInstance(), true));
        }
        sender.sendMessage(ChatColor.YELLOW + "You are " + (sender.hasMetadata("build") ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " in build mode.");
    }

}

