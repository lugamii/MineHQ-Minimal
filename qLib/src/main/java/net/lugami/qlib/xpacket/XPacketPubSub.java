package net.lugami.qlib.xpacket;

import net.lugami.qlib.qLib;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

@NoArgsConstructor
public class XPacketPubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        Class<?> packetClass;
        int packetMessageSplit = message.indexOf("||");
        String packetClassStr = message.substring(0, packetMessageSplit);
        String messageJson = message.substring(packetMessageSplit + "||".length());
        try {
            packetClass = Class.forName(packetClassStr);
        }
        catch (ClassNotFoundException ignored) {
            return;
        }
        XPacket packet = (XPacket) qLib.PLAIN_GSON.fromJson(messageJson, packetClass);
        if (qLib.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask(qLib.getInstance(), packet::onReceive);
        }
    }
}

