package net.lugami.bridge.global.packet;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class PacketHandler {

    private static JedisPool jedisPool;
    private static String jedisChannel;

    public static void init(JedisPool pool, String channel) {
        jedisPool = pool;
        jedisChannel = channel;
        connectToServer(pool);
    }

    public static void connectToServer(JedisPool connectTo) {
        Thread subscribeThread = new Thread(() -> {
            while (true) {
                try {
                    Jedis jedis = connectTo.getResource();
                    Throwable throwable = null;
                    try {
                        PacketPubSub pubSub = new PacketPubSub();
                        jedis.subscribe(pubSub, jedisChannel);
                    }
                    catch (Throwable pubSub) {
                        throwable = pubSub;
                        throw pubSub;
                    }
                    finally {
                        if (jedis == null) continue;
                        if (throwable != null) {
                            try {
                                jedis.close();
                            }
                            catch (Throwable pubSub) {
                                throwable.addSuppressed(pubSub);
                            }
                            continue;
                        }
                        jedis.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Bridge - Packet Subscribe Thread");
        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    public static void sendToAll(Packet packet) {
        PacketHandler.send(packet, jedisPool);
    }

    public static void send(Packet packet, JedisPool sendOn) {

        new Thread(() -> {
            try (Jedis jedis = sendOn.getResource();){
                String encodedPacket = packet.getClass().getName() + "||" + new Gson().toJson(packet);
                jedis.publish(jedisChannel, encodedPacket);
            }
        }).start();

    }

    private PacketHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

