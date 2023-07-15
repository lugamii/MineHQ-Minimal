package net.lugami.bridge.global.packet.types;

import org.bukkit.Bukkit;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.Packet;

public class ServerGroupCommandPacket implements Packet {

    private final String serverGroup;
    private final String command;

    public ServerGroupCommandPacket(String serverGroup, String command) {
        this.serverGroup = serverGroup;
        this.command = command;
    }

    @Override
    public void onReceive() {
        if (BridgeGlobal.getServerHandler().getServer(Bukkit.getServerName()).getGroup().equals(serverGroup)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}