package net.lugami.bridge.bukkit.parameters;

import net.lugami.qlib.xpacket.XPacket;
import org.bukkit.Bukkit;

import java.util.UUID;


public class WhitelistPacket implements XPacket {

    private String server;
    private boolean whitelist;
    private UUID uuid;

    public WhitelistPacket(String server, boolean whitelist) {
        this.server = server;
        this.whitelist = whitelist;
    }

    public WhitelistPacket(String server, UUID uuid) {
        this.server = server;
        this.uuid = uuid;
    }

    @Override
    public void onReceive() {
        if(uuid == null) {
            if(Bukkit.getServerName().equalsIgnoreCase(server)) {
                Bukkit.setWhitelist(whitelist);
            }
        }else {
            if(Bukkit.getServerName().equalsIgnoreCase(server)) {
                Bukkit.getOfflinePlayer(uuid).setWhitelisted(true);
            }
        }

    }
}
