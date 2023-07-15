package net.lugami.bridge.bukkit.commands.server;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.ServerShutdownPacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.entity.Player;
import net.lugami.bridge.global.packet.PacketHandler;

public class ShutdownCommand {

    @Command(names = {"shutdown"}, permission = "bridge.server.shutdown", description = "Shutdown a server", hidden = true)
    public static void shutdown(Player s, @Param(name = "server") String serverName) {
        PacketHandler.sendToAll(new ServerShutdownPacket(serverName));
        if (serverName.equalsIgnoreCase("all")) {
            PacketHandler.sendToAll(new NetworkBroadcastPacket("basic.staff", "&8[&eServer Monitor&8] &fRemoving all servers..."));
        } else {
            PacketHandler.sendToAll(new NetworkBroadcastPacket("basic.staff", "&8[&eServer Monitor&8] &fRemoving server " + serverName + "..."));
        }
    }
}
