package net.lugami.bridge.bukkit.commands.server.serverinfo;

import mkremins.fanciful.FancyMessage;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.TPSUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.status.BridgeServer;

public class ServerInfoCommand {

    @Command(names = {"serverinfo info"}, permission = "bridge.server.info", description = "Get information about a server group", hidden = true)
    public static void serverInfo(CommandSender s, @Param(name = "server", wildcard = true) String server) {
        if (BridgeGlobal.getServerHandler().getServer(server) == null) {
            s.sendMessage(ChatColor.RED + "There is no such server with the name \"" + server + "\".");
            return;
        }
        BridgeServer bridgeServer = BridgeGlobal.getServerHandler().getServer(server);
        s.sendMessage(StringUtils.repeat(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-", 45));
        s.sendMessage(ChatColor.GOLD + bridgeServer.getName() + ChatColor.GRAY + " [" + bridgeServer.getOnline() + '/' + bridgeServer.getMaximum() + "]");
        s.sendMessage(ChatColor.YELLOW + "Provider: " + ChatColor.RED + bridgeServer.getProvider());
        s.sendMessage(ChatColor.YELLOW + "Status: " + bridgeServer.formattedStatus(true));
        if (!bridgeServer.getMotd().isEmpty())
            s.sendMessage(ChatColor.YELLOW + "MOTD: " + ChatColor.RED + bridgeServer.getMotd());
        if (bridgeServer.getTps() != 0.0)
            s.sendMessage(ChatColor.YELLOW + "TPS: " + ChatColor.RED + TPSUtils.formatTPS(bridgeServer.getTps(), true));
        if (bridgeServer.getSystemType().equals("BUKKIT")) {
            boolean wl = bridgeServer.isWhitelisted();
            new FancyMessage(ChatColor.YELLOW + "Whitelisted: ").then(ChatColor.RED + (wl ? "True" : "False")).tooltip(ChatColor.AQUA + "Click to change to change whitelist status.").command("/serverinfo whitelist " + bridgeServer.getName()).send(s);
        }
        s.sendMessage(StringUtils.repeat(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-", 45));
    }
}
