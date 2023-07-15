package net.lugami.bridge.global.packet.types;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.deployment.DeploymentHandler;
import net.lugami.bridge.global.packet.Packet;
import net.md_5.bungee.BungeeCord;
import net.lugami.bridge.global.packet.PacketHandler;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDeployPacket implements Packet {

    private String serverName;
    private List<File> plugins;

    @Override
    public void onReceive() {
        if (!BridgeGlobal.isHandleServerDeployment()) {
            BridgeGlobal.sendLog("debug");
            return;
        }
        if (DeploymentHandler.doesServerExit(serverName)) {
            BridgeGlobal.sendLog("Server with name already exists deploy failed.");
            return;
        }

        DeploymentHandler.createServer(serverName, BungeeCord.getInstance().getServers().values().stream().sorted(Comparator.comparingInt(o -> o.getAddress().getPort())).collect(Collectors.toList()).get(0).getAddress().getPort() + 1, plugins);
        BridgeGlobal.sendLog("Attempting to deploy server with name \"" + serverName + "\" now.");
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&aAttempting to deploy server with name \"" + serverName + "\" now."));
    }

    public ServerDeployPacket(String serverName, List<File> plugins) {
        this.serverName = serverName;
        this.plugins = plugins;
    }
}

