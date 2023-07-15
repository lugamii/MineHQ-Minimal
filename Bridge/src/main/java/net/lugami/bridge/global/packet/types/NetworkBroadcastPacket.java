package net.lugami.bridge.global.packet.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.packet.Packet;

public class NetworkBroadcastPacket implements Packet {

    public String permission;
    public String message;

    public NetworkBroadcastPacket(String permission, String message) {
        this.permission = permission;
        this.message = message;
    }

    @Override
    public void onReceive() {
        Bukkit.getOnlinePlayers().stream().filter(player -> BukkitAPI.getProfile(player).hasPermission(permission)).forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }
}