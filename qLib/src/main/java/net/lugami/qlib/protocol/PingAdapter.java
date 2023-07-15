package net.lugami.qlib.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import net.lugami.qlib.qLib;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PingAdapter extends PacketAdapter implements Listener {

    private static final Map<UUID, PingCallback> callbacks;
    private static final Map<UUID, Integer> ping;
    private static final Map<UUID, Integer> lastReply;

    public PingAdapter() {
        super(qLib.getInstance(), PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE);
    }

    public void onPacketSending(final PacketEvent event) {
        final int id = event.getPacket().getIntegers().read(0);
        PingAdapter.callbacks.put(event.getPlayer().getUniqueId(), new PingCallback(id) {
            @Override
            public void call() {
                final int ping = (int)(System.currentTimeMillis() - this.getSendTime());
                PingAdapter.ping.put(event.getPlayer().getUniqueId(), ping);
                PingAdapter.lastReply.put(event.getPlayer().getUniqueId(), MinecraftServer.currentTick);
            }
        });
    }

    public void onPacketReceiving(final PacketEvent event) {
        final Iterator<Map.Entry<UUID, PingCallback>> iterator = PingAdapter.callbacks.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, PingCallback> entry = iterator.next();
            if (entry.getValue().getId() == event.getPacket().getIntegers().read(0)) {
                entry.getValue().call();
                iterator.remove();
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        PingAdapter.ping.remove(event.getPlayer().getUniqueId());
        PingAdapter.lastReply.remove(event.getPlayer().getUniqueId());
        PingAdapter.callbacks.remove(event.getPlayer().getUniqueId());
    }

    public static int getAveragePing() {
        if (PingAdapter.ping.size() == 0) {
            return 0;
        }
        int x = 0;
        for (final int p : PingAdapter.ping.values()) {
            x += p;
        }
        return x / PingAdapter.ping.size();
    }

    public static Map<UUID, Integer> getPing() {
        return PingAdapter.ping;
    }

    public static Map<UUID, Integer> getLastReply() {
        return PingAdapter.lastReply;
    }

    static {
        callbacks = Maps.newConcurrentMap();
        ping = Maps.newConcurrentMap();
        lastReply = Maps.newConcurrentMap();
    }

    private abstract static class PingCallback
    {
        private final long sendTime;
        private final int id;

        public abstract void call();

        @ConstructorProperties({ "id" })
        public PingCallback(final int id) {
            this.sendTime = System.currentTimeMillis();
            this.id = id;
        }

        public long getSendTime() {
            return this.sendTime;
        }

        public int getId() {
            return this.id;
        }
    }
}