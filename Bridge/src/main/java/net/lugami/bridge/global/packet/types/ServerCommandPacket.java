package net.lugami.bridge.global.packet.types;

import net.lugami.bridge.global.packet.Packet;
import org.bukkit.Bukkit;

public class ServerCommandPacket implements Packet {

    private final String server;
    private final String command;

    public ServerCommandPacket(String server, String command) {
        this.server = server;
        this.command = command;
    }

    @Override
    public void onReceive() {
        if (Bukkit.getServerName().equals(server)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else if (server.equals("globally")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}