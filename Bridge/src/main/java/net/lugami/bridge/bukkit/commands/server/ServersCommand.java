package net.lugami.bridge.bukkit.commands.server;

import net.lugami.bridge.bukkit.commands.server.menu.server.ServerMenu;
import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;

public class ServersCommand {

    @Command(names = "servers", permission = "bridge.server.list", description = "View all online servers", hidden = true)
    public static void servers(Player player) {
        if (BridgeGlobal.getServerHandler().getServers().isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no servers found...");
            return;
        }
        new ServerMenu().openMenu(player);
    }
}
