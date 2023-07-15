package com.lunarclient.bukkitapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.lunarclient.bukkitapi.event.*;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.client.*;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.nethandler.server.LCNetHandlerServer;
import com.lunarclient.bukkitapi.nethandler.server.LCPacketStaffModStatus;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketEmoteBroadcast;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import com.lunarclient.bukkitapi.object.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class LunarClientAPI extends JavaPlugin implements Listener {

    private static final String MESSAGE_CHANNEL = "Lunar-Client";
    private static final String FROM_BUNGEE_CHANNEL = "LC|ACU";

    @Getter private static LunarClientAPI instance;
    private final Set<UUID> playersRunningLunarClient = Sets.newHashSet();
    private final Set<UUID> playersRunningAntiCheat = Sets.newHashSet();

    private final Set<UUID> playersNotRegistered = new HashSet<>();
    private final Map<UUID, List<LCPacket>> packetQueue = new HashMap<>();
    private final Map<UUID, Function<World, String>> worldIdentifiers = new HashMap<>();
    @Setter private LCNetHandlerServer netHandlerServer = new LCNetHandlerServer() {

        @Override
        public void handleStaffModStatus(LCPacketStaffModStatus lcPacketStaffModStatus) {

        }

        @Override
        public void handleVoice(LCPacketClientVoice lcPacketClientVoice) {

        }

        @Override
        public void handleVoiceMute(LCPacketVoiceMute lcPacketVoiceMute) {

        }

        @Override
        public void handleVoiceChannelSwitch(LCPacketVoiceChannelSwitch lcPacketVoiceChannelSwitch) {

        }

        @Override
        public void handleAddWaypoint(LCPacketWaypointAdd lcPacketWaypointAdd) {

        }

        @Override
        public void handleRemoveWaypoint(LCPacketWaypointRemove lcPacketWaypointRemove) {

        }

        @Override
        public void handleEmote(LCPacketEmoteBroadcast lcPacketEmoteBroadcast) {

        }

    };
    private final Map<UUID, LCAntiCheatStatusEvent.Status> preJoinStatuses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        Messenger messenger = getServer().getMessenger();

        messenger.registerOutgoingPluginChannel(this, MESSAGE_CHANNEL);
        messenger.registerIncomingPluginChannel(this, MESSAGE_CHANNEL, (channel, player, bytes) -> {
            LCPacket packet = LCPacket.handle(bytes, player);
            Bukkit.getPluginManager().callEvent(new LCPacketReceivedEvent(player, packet));
            packet.process(netHandlerServer);
        });

        messenger.registerIncomingPluginChannel(this, FROM_BUNGEE_CHANNEL, (channel, p, bytes) -> {
            String[] payload = new String(bytes, UTF_8).split(":");
            UUID uuid = UUID.fromString(payload[0]);
            boolean prot = Boolean.parseBoolean(payload[1]);

            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                anticheatUpdate(player, prot ? LCAntiCheatStatusEvent.Status.PROTECTED : LCAntiCheatStatusEvent.Status.UNPROTECTED);
            } else {
                preJoinStatuses.put(uuid, prot ? LCAntiCheatStatusEvent.Status.PROTECTED : LCAntiCheatStatusEvent.Status.UNPROTECTED);
            }
        });

        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerJoin(PlayerJoinEvent event) {
                if (preJoinStatuses.containsKey(event.getPlayer().getUniqueId())) {
                    anticheatUpdate(event.getPlayer(), preJoinStatuses.remove(event.getPlayer().getUniqueId()));
                }
            }

            @EventHandler
            public void onRegister(PlayerRegisterChannelEvent event) {
                if (!event.getChannel().equals(MESSAGE_CHANNEL)) {
                    return;
                }

                playersNotRegistered.remove(event.getPlayer().getUniqueId());
                playersRunningLunarClient.add(event.getPlayer().getUniqueId());

                if (packetQueue.containsKey(event.getPlayer().getUniqueId())) {
                    packetQueue.get(event.getPlayer().getUniqueId()).forEach(p -> {
                        sendPacket(event.getPlayer(), p);
                    });

                    packetQueue.remove(event.getPlayer().getUniqueId());
                }

                getServer().getPluginManager().callEvent(new LCPlayerRegisterEvent(event.getPlayer()));
                updateWorld(event.getPlayer());
            }

            @EventHandler
            public void onUnregister(PlayerUnregisterChannelEvent event) {
                if (event.getChannel().equals(MESSAGE_CHANNEL)) {
                    playersRunningLunarClient.remove(event.getPlayer().getUniqueId());
                    getServer().getPluginManager().callEvent(new LCPlayerUnregisterEvent(event.getPlayer()));
                }
            }

            @EventHandler
            public void onUnregister(PlayerQuitEvent event) {
                playersRunningLunarClient.remove(event.getPlayer().getUniqueId());
                playersNotRegistered.remove(event.getPlayer().getUniqueId());
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onJoin(PlayerJoinEvent event) {
                Bukkit.getScheduler().runTaskLater(instance, () -> {
                    if (!isRunningLunarClient(event.getPlayer())) {
                        playersNotRegistered.add(event.getPlayer().getUniqueId());
                        packetQueue.remove(event.getPlayer().getUniqueId());
                    }
                }, 2 * 20L);
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onWorldChange(PlayerChangedWorldEvent event) {
                updateWorld(event.getPlayer());
            }

            private void updateWorld(Player player) {
                String worldIdentifier = getWorldIdentifier(player.getWorld());

                sendPacket(player, new LCPacketUpdateWorld(worldIdentifier));
            }

        }, this);
    }

    public boolean isRunningAntiCheat(Player player) {
        return isRunningAntiCheat(player.getUniqueId());
    }

    public boolean isRunningAntiCheat(UUID playerUuid) {
        return playersRunningAntiCheat.contains(playerUuid);
    }

    public void anticheatUpdate(Player player, LCAntiCheatStatusEvent.Status status) {
        if (!playersRunningAntiCheat.contains(player.getUniqueId()) && status == LCAntiCheatStatusEvent.Status.PROTECTED) {
            playersRunningAntiCheat.add(player.getUniqueId());
            Bukkit.getPluginManager().callEvent(new LCAntiCheatStatusEvent(player, status));
        } else if (playersRunningAntiCheat.contains(player.getUniqueId()) && status == LCAntiCheatStatusEvent.Status.UNPROTECTED) {
            playersRunningAntiCheat.remove(player.getUniqueId());
            Bukkit.getPluginManager().callEvent(new LCAntiCheatStatusEvent(player, status));
        }
    }

    public boolean isRunningLunarClient(Player player) {
        return isRunningLunarClient(player.getUniqueId());
    }

    public boolean isRunningLunarClient(UUID playerUuid) {
        return playersRunningLunarClient.contains(playerUuid);
    }

    public Set<Player> getPlayersRunningAntiCheat() {
        return ImmutableSet.copyOf(playersRunningAntiCheat.stream().map(Bukkit::getPlayer).collect(Collectors.toSet()));
    }

    public Set<Player> getPlayersRunningLunarClient() {
        return ImmutableSet.copyOf(playersRunningLunarClient.stream().map(Bukkit::getPlayer).collect(Collectors.toSet()));
    }

    public void sendNotification(Player player, LCNotification notification) {
        sendPacket(player, new LCPacketNotification(notification.getMessage(), notification.getDurationMs(), notification.getLevel().name()));
    }

    public String getWorldIdentifier(World world) {
        String worldIdentifier = world.getUID().toString();

        if (worldIdentifiers.containsKey(world.getUID())) {
            worldIdentifier = worldIdentifiers.get(world.getUID()).apply(world);
        }

        return worldIdentifier;
    }

    public void registerWorldIdentifier(World world, Function<World, String> identifier) {
        worldIdentifiers.put(world.getUID(), identifier);
    }

    public void sendNotificationOrFallback(Player player, LCNotification notification, Runnable fallback) {
        if (isRunningLunarClient(player)) {
            sendNotification(player, notification);
        } else {
            fallback.run();
        }
    }

    public void setStaffModuleState(Player player, StaffModule module, boolean state) {
        sendPacket(player, new LCPacketStaffModState(module.name(), state));
    }

    public void setMinimapStatus(Player player, MinimapStatus status) {
        sendPacket(player, new LCPacketServerRule(ServerRule.MINIMAP_STATUS, status.name()));
    }

    public void setCompetitiveGame(Player player, boolean isCompetitive) {
        sendPacket(player, new LCPacketServerRule(ServerRule.COMPETITIVE_GAME, isCompetitive));
    }

    public void giveAllStaffModules(Player player) {
        for (StaffModule module : StaffModule.values()) {
            LunarClientAPI.getInstance().setStaffModuleState(player, module, true);
        }

        sendNotification(player, new LCNotification("Staff modules enabled", Duration.ofSeconds(3)));
    }

    public void disableAllStaffModules(Player player) {
        for (StaffModule module : StaffModule.values()) {
            LunarClientAPI.getInstance().setStaffModuleState(player, module, false);
        }

        sendNotification(player, new LCNotification("Staff modules disabled", Duration.ofSeconds(3)));
    }

    public void sendTeammates(Player player, LCPacketTeammates packet) {
        validatePlayers(player, packet);
        sendPacket(player, packet);
    }

    public void validatePlayers(Player sendingTo, LCPacketTeammates packet) {
        packet.getPlayers().entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey()) != null && !Bukkit.getPlayer(entry.getKey()).getWorld().equals(sendingTo.getWorld()));
    }

    public void addHologram(Player player, UUID id, Vector position, String[] lines) {
        sendPacket(player, new LCPacketHologram(id, position.getX(), position.getY(), position.getZ(), Arrays.asList(lines)));
    }

    public void updateHologram(Player player, UUID id, String[] lines) {
        sendPacket(player, new LCPacketHologramUpdate(id, Arrays.asList(lines)));
    }

    public void removeHologram(Player player, UUID id) {
        sendPacket(player, new LCPacketHologramRemove(id));
    }

    public void overrideNametag(Player target, List<String> nametag, Player viewer) {
        sendPacket(viewer, new LCPacketNametagsOverride(target.getUniqueId(), nametag));
    }

    public void resetNametag(Player target, Player viewer) {
        sendPacket(viewer, new LCPacketNametagsOverride(target.getUniqueId(), null));
    }

    public void hideNametag(Player target, Player viewer) {
        sendPacket(viewer, new LCPacketNametagsOverride(target.getUniqueId(), ImmutableList.of()));
    }

    public void sendTitle(Player player, TitleType type, String message, Duration displayTime) {
        sendTitle(player, type, message, Duration.ofMillis(500), displayTime, Duration.ofMillis(500));
    }

    public void sendTitle(Player player, TitleType type, String message, float scale, Duration displayTime) {
        sendTitle(player, type, message, scale, Duration.ofMillis(500), displayTime, Duration.ofMillis(500));
    }

    public void sendTitle(Player player, TitleType type, String message, Duration fadeInTime, Duration displayTime, Duration fadeOutTime) {
        sendTitle(player, type, message, 1F, fadeInTime, displayTime, fadeOutTime);
    }

    public void sendTitle(Player player, TitleType type, String message, float scale, Duration fadeInTime, Duration displayTime, Duration fadeOutTime) {
        sendPacket(player, new LCPacketTitle(type.name().toLowerCase(), message, scale, displayTime.toMillis(), fadeInTime.toMillis(), fadeOutTime.toMillis()));
    }

    public void sendWaypoint(Player player, LCWaypoint waypoint) {
        sendPacket(player, new LCPacketWaypointAdd(waypoint.getName(), waypoint.getWorld(), waypoint.getColor(), waypoint.getX(), waypoint.getY(), waypoint.getZ(), waypoint.isForced(), waypoint.isVisible()));
    }

    public void removeWaypoint(Player player, LCWaypoint waypoint) {
        sendPacket(player, new LCPacketWaypointRemove(waypoint.getName(), waypoint.getWorld()));
    }

    public void sendCooldown(Player player, LCCooldown cooldown) {
        sendPacket(player, new LCPacketCooldown(cooldown.getMessage(), cooldown.getDurationMs(), cooldown.getIcon().getId()));
    }

    public void sendGhost(Player player, LCGhost ghost) {
        sendPacket(player, new LCPacketGhost(ghost.getGhostedPlayers(), ghost.getUnGhostedPlayers()));
    }

    public void clearCooldown(Player player, LCCooldown cooldown) {
        sendPacket(player, new LCPacketCooldown(cooldown.getMessage(), 0L, cooldown.getIcon().getId()));
    }

    public void setBossbar(Player player, String text, float health) {
        sendPacket(player, new LCPacketBossBar(0, text, health));
    }

    public void unsetBossbar(Player player) {
        sendPacket(player, new LCPacketBossBar(1, null, 0));
    }

    /*
     *  This is a boolean to indicate whether or not a LC message was sent.
     *  An example use-case is when you want to send a Lunar Client
     *  notification if a player is running Lunar Client, and a chat
     *  message if not.
     */
    public boolean sendPacket(Player player, LCPacket packet) {
        if (isRunningLunarClient(player)) {
            player.sendPluginMessage(this, MESSAGE_CHANNEL, LCPacket.getPacketData(packet));
            Bukkit.getPluginManager().callEvent(new LCPacketSentEvent(player, packet));
            return true;
        } else if (!playersNotRegistered.contains(player.getUniqueId())) {
            packetQueue.putIfAbsent(player.getUniqueId(), new ArrayList<>());
            packetQueue.get(player.getUniqueId()).add(packet);
            return false;
        }
        return false;
    }

}
