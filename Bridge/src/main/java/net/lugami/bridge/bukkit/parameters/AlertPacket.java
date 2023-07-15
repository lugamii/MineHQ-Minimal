package net.lugami.bridge.bukkit.parameters;

import net.lugami.qlib.xpacket.XPacket;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;


public class AlertPacket implements XPacket {

    private String message;
    private boolean raw;

    public AlertPacket(String message, boolean raw) {
        this.message = message;
        this.raw = raw;
    }


    @Override
    public void onReceive() {
        Bukkit.broadcastMessage((raw ? "" : ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Alert" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE) + ChatColor.translateAlternateColorCodes('&', message));
    }
}
