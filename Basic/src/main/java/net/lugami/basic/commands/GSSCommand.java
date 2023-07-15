package net.lugami.basic.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.lugami.basic.Basic;
import net.lugami.basic.commands.parameter.MonitorTarget;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GSSCommand implements Listener {

    private static Map<UUID, UUID> monitoring = new HashMap<UUID, UUID>();
    private static Map<UUID, Map<Integer, Long>> sentAt = new HashMap<UUID, Map<Integer, Long>>();

    @Command(names={"gss"}, permission="basic.gss")
    public static void gss(Player sender, @Param(name="player", defaultValue="-") MonitorTarget target) {
        if (target.isStop()) {
            monitoring.remove(sender.getUniqueId());
        } else {
            monitoring.put(sender.getUniqueId(), target.getPlayer().getUniqueId());
            sender.chat("/ss " + target.getName());
        }
    }

    @Command(names={"monitor"}, permission="basic.gss")
    public static void monitor(Player sender, @Param(name="player", defaultValue="-") MonitorTarget target) {
        if (target.isStop()) {
            monitoring.remove(sender.getUniqueId());
        } else {
            monitoring.put(sender.getUniqueId(), target.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        monitoring.remove(player.getUniqueId());
        monitoring.entrySet().removeIf(entry -> entry.getValue() == player.getUniqueId());
    }

    public static void registerAdapter() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Basic.getInstance(), new PacketType[]{PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE}){

            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                int keepAliveId = event.getPacket().getIntegers().read(0);
                if (monitoring.containsValue(player.getUniqueId())) {
                    sentAt.putIfAbsent(player.getUniqueId(), new HashMap<>());
                    (sentAt.get(player.getUniqueId())).put(keepAliveId, System.currentTimeMillis());
                }
            }

            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                int keepAliveId = event.getPacket().getIntegers().read(0);
                if (sentAt.containsKey(player.getUniqueId()) && (sentAt.get(player.getUniqueId())).containsKey(keepAliveId)) {
                    long diff = System.currentTimeMillis() - (sentAt.get(player.getUniqueId())).get(keepAliveId);
                    for (Map.Entry<UUID, UUID> entry : monitoring.entrySet()) {
                        Player watcher = Bukkit.getPlayer((entry.getKey()));
                        if (entry.getValue() != player.getUniqueId() || watcher == null) continue;
                        if (diff < 400L) {
                            watcher.sendMessage("[Monitor] " + player.getName() + " (" + diff + "ms)");
                            continue;
                        }
                        watcher.sendMessage(ChatColor.RED + "[Monitor] " + player.getName() + " (" + diff + "ms)");
                    }
                }
            }
        });
    }
}

