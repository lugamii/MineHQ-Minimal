package net.lugami.bridge.global.packet;

import com.google.gson.Gson;
import redis.clients.jedis.JedisPubSub;

final class PacketPubSub
extends JedisPubSub {
    PacketPubSub() {
    }

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
        Packet packet = (Packet) new Gson().fromJson(messageJson, packetClass);
        if(packet != null) {
            packet.onReceive();
        }
    }
}

