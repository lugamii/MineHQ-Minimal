package net.lugami.bridge.bukkit.commands.server;

import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;

public class NetworkStatsCommand {

    @Command(names = "networkstats", permission = "bridge.server.stats", description = "View all online servers", hidden = true)
    public static void networkstats(Player player) {
        player.sendMessage(ChatColor.BLUE + "Network Stats");
        player.sendMessage(ChatColor.YELLOW + "Online: " + ChatColor.WHITE + BridgeGlobal.getServerHandler().getServer("BungeeCord").getOnline());
        player.sendMessage(ChatColor.YELLOW + "Total Profiles: " + ChatColor.WHITE + BridgeGlobal.getMongoHandler().getProfiles().size());
        player.sendMessage(ChatColor.YELLOW + "Total Ranks: " + ChatColor.WHITE + BridgeGlobal.getRankHandler().getRanks().size());
        player.sendMessage(ChatColor.YELLOW + "Online Servers " + ChatColor.WHITE + BridgeGlobal.getServerHandler().getServers().size());
        player.sendMessage(ChatColor.YELLOW + "Disguised Players: " + ChatColor.WHITE + BridgeGlobal.getDisguiseManager().getDisguisePlayers().size());

    }
}
