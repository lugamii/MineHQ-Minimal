package net.lugami.bridge.bukkit.commands;

import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.status.BridgeServer;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;

public class FindCommand {

    @Command(names = { "find" }, permission = "bridge.find", description = "See the server a player is currently playing on", async = true)
    public static void find(CommandSender sender, @Param(name = "player", extraData = "get") Profile profile) {
        findresponse(sender, profile);
    }

    public static void findresponse(CommandSender sender, Profile profile) {
        BridgeServer server = BridgeGlobal.getServerHandler().findPlayerServer(profile.getUuid());
        BridgeServer proxy = BridgeGlobal.getServerHandler().findPlayerProxy(profile.getUuid());
        if (server == null) {
            sender.sendMessage(ChatColor.RED + (profile.getDisguise() != null ? profile.getDisguise().getDisguiseName() : profile.getUsername()) + " is currently not on the network.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + (profile.getDisguise() != null ? profile.getDisguise().getDisguiseName() : profile.getUsername()) +  " is on " + ChatColor.GREEN + server.getName() + (proxy != null ? ChatColor.YELLOW + " (" + ChatColor.GREEN + proxy.getName() + " Proxy" + ChatColor.YELLOW + ")" : ""));

    }
}
