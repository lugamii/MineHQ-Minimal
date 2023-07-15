package net.minecraft.server;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.crypto.SecretKey;

import net.minecraft.util.com.google.common.collect.Queues;
import net.minecraft.util.com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.util.io.netty.channel.local.LocalChannel;
import net.minecraft.util.io.netty.channel.local.LocalServerChannel;
import net.minecraft.util.io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.io.netty.handler.timeout.TimeoutException;
import net.minecraft.util.io.netty.util.AttributeKey;
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
// Spigot start
import com.google.common.collect.ImmutableSet;
import org.spigotmc.SpigotCompressor;
import org.spigotmc.SpigotConfig;
import org.spigotmc.SpigotDecompressor;
// Spigot end
// Guardian start
import org.bukkit.event.player.GuardianEvent;
import org.bukkit.Bukkit;
import net.minecraft.util.com.google.common.base.Joiner;
import java.util.Iterator;
// Guardian end

// Poweruser start
import org.spigotmc.CustomTimingsHandler;
import org.bukkit.craftbukkit.SpigotTimings;
// Poweruser end

public class NetworkManager extends SimpleChannelInboundHandler {

    private static final Logger i = LogManager.getLogger();
    public static final Marker a = MarkerManager.getMarker("NETWORK");
    public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", a);
    public static final Marker c = MarkerManager.getMarker("NETWORK_STAT", a);
    public static final AttributeKey d = new AttributeKey("protocol");
    public static final AttributeKey e = new AttributeKey("receivable_packets");
    public static final AttributeKey f = new AttributeKey("sendable_packets");
    public static final NioEventLoopGroup g = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    public static final NetworkStatistics h = new NetworkStatistics();
    private final boolean j;
    private final Queue k = Queues.newConcurrentLinkedQueue();
    // private final Queue l = Queues.newConcurrentLinkedQueue(); // MineHQ
    private Channel m;
    // Spigot Start
    public SocketAddress n;
    public java.util.UUID spoofedUUID;
    public Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener o;
    private EnumProtocol p;
    private IChatBaseComponent q;
    private boolean r;
    // Spigot Start
    public static final AttributeKey<Integer> protocolVersion = new AttributeKey<Integer>("protocol_version");
    public static final ImmutableSet<Integer> SUPPORTED_VERSIONS = ImmutableSet.of(4, 5, 47);
    public static final int CURRENT_VERSION = 5;
    public static int getVersion(Channel attr)
    {
        Integer ver = attr.attr( protocolVersion ).get();
        return ( ver != null ) ? ver : CURRENT_VERSION;
    }
    public int getVersion()
    {
        return getVersion( this.m );
    }
    // Spigot End

    // Poweruser start
    private boolean lockDownIncomingTraffic = false;

    protected boolean lockDownIncomingTraffic() {
        boolean oldValue = this.lockDownIncomingTraffic;
        this.lockDownIncomingTraffic = true;
        return oldValue;
    }
    // Poweruser end

    // Guardian start
    private Packet[] packets = new Packet[10];
    private long[] limitTimes = new long[12];
    public long lastTickNetworkProcessed = MinecraftServer.currentTick;
    public long ticksSinceLastPacket = -1L;
    private int numOfKillAuraB = 0;
    private List<Long> numOfKillAuraBLogs = new ArrayList<Long>();
    private int numOfT = 0;
    private List<Long> numOfKillAuraTLogs = new ArrayList<Long>();
    private long lastKillAuraKTick = MinecraftServer.currentTick;
    public long currentTime = System.currentTimeMillis();
    public long lastVehicleTime = -1L;
    public int numOfFlyingPacketsInARow = 0;
    // Guardian end

    public static final GenericFutureListener[] emptyListenerArray = new GenericFutureListener[0]; // Poweruser

    public NetworkManager(boolean flag) {
        this.j = flag;

        // Guardian start
        this.limitTimes[0] = 4000L;
        this.limitTimes[1] = 4000L;
        this.limitTimes[2] = 4000L;
        this.limitTimes[3] = 4000L;
        this.limitTimes[4] = 5000L;
        this.limitTimes[5] = 6000L;
        this.limitTimes[6] = 7000L;
        this.limitTimes[7] = 7000L;
        this.limitTimes[8] = 7000L;
        this.limitTimes[9] = 7000L;
        this.limitTimes[10] = 7000L;
        this.limitTimes[11] = 7000L;
        // Guardian end
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception { // CraftBukkit - throws Exception
        super.channelActive(channelhandlercontext);
        this.m = channelhandlercontext.channel();
        this.n = this.m.remoteAddress();
        // Spigot Start
        this.preparing = false;
        // Spigot End
        this.a(EnumProtocol.HANDSHAKING);
    }

    public void a(EnumProtocol enumprotocol) {
        this.p = (EnumProtocol) this.m.attr(d).getAndSet(enumprotocol);
        this.m.attr(e).set(enumprotocol.a(this.j));
        this.m.attr(f).set(enumprotocol.b(this.j));
        this.m.config().setAutoRead(true);
        i.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        ChatMessage chatmessage;

        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});
        }

        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {
        if (this.m.isOpen() && !this.lockDownIncomingTraffic) { // Poweruser
            if (packet.a()) {
                packet.handle(this.o);
                // Guardian start
                if (packet instanceof PacketPlayInKeepAlive) {
                    this.k.add(packet);
                }
                // Guardian end
            } else {
                // Guardian start
                if ((this.o instanceof PlayerConnection)) {
                    PlayerConnection connection = (PlayerConnection)this.o;

                    if (((packet instanceof PacketPlayInChat)) || ((packet instanceof PacketPlayInCustomPayload))) {
                        this.k.add(packet);
                        return;
                    }

                    if ((packet instanceof PacketPlayInFlying)) {
                        connection.movesReceived += 1L;
                        connection.killAuraXSwing = false;
                        if (packet.getClass() != PacketPlayInFlying.class) {
                            this.numOfFlyingPacketsInARow = 0;
                        } else if (++this.numOfFlyingPacketsInARow == 21 && Bukkit.shouldGuardianAct()) {
                            String message = connection.getPlayer().getName() + " is using Regen (Module B)";
                            runSync(
                                    new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.REGENERATION, "B", GuardianEvent.DisplayLevel.HIGH, message)
                            );
                        }
                    }

                    if (!connection.player.abilities.canInstantlyBuild) {
                        if ((connection.lastAttackPlayerTime == 0L) || (this.currentTime - connection.lastAttackPlayerTime > 5000L) || (!Bukkit.shouldGuardianAct())) {
                            connection.autoclickerAStage = (connection.autoclickerAThreshold = 0);
                            connection.autoClickerBStage = (connection.autoClickerBThreshold = connection.killAuraTypePOther = 0);
                            connection.killAuraNStage = (connection.killAuraQThreshold = 0);
                        } else {
                            // Autoclicker Module A
                            if (connection.autoclickerAStage == 0) {
                                if ((packet instanceof PacketPlayInArmAnimation)) {
                                    connection.autoclickerAStage = 1;
                                }
                            } else if (connection.autoclickerAStage == 1) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 0)) {
                                    connection.autoclickerAStage = 2;
                                } else {
                                    connection.autoclickerAStage = 0;
                                }
                            } else if (connection.autoclickerAStage == 2) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 1)) {
                                    if (++connection.autoclickerAThreshold == 5) {
                                        connection.autoclickerAThreshold = 0;

                                        String message = connection.getPlayer().getName() + " is using Autoclicker (Module A)";
                                        runSync(
                                                new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.AUTO_CLICKER, "A", GuardianEvent.DisplayLevel.HIGHEST, message)
                                        );
                                    }
                                } else {
                                    connection.autoclickerAThreshold = 0;
                                }
                                connection.autoclickerAStage = 0;
                            }

                            // Autoclicker Module B
                            if (connection.autoClickerBStage == 0) {
                                if ((packet instanceof PacketPlayInArmAnimation)) {
                                    connection.autoClickerBStage = 1;
                                }
                            } else if (connection.autoClickerBStage == 1) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 0)) {
                                    connection.autoClickerBStage = 2;
                                } else {
                                    connection.autoClickerBStage = 0;
                                }
                            } else if (connection.autoClickerBStage == 2) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 1)) {
                                    if (++connection.autoClickerBThreshold == 5) {
                                        if (connection.autoClickerBStage > 0) {
                                            String message = connection.getPlayer().getName() + " is using Autoclicker (Module B) [" + connection.killAuraTypePOther + "]";
                                            runSync(
                                                    new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.AUTO_CLICKER, "B", GuardianEvent.DisplayLevel.HIGHEST, message)
                                                            .addData("other", connection.killAuraTypePOther)
                                            );
                                        }
                                        connection.autoClickerBThreshold = (connection.killAuraTypePOther = 0);
                                    }
                                    connection.autoClickerBStage = 0;
                                } else if ((packet instanceof PacketPlayInArmAnimation)) {
                                    connection.autoClickerBStage = 3;
                                } else {
                                    connection.autoClickerBStage = (connection.autoClickerBThreshold = connection.killAuraTypePOther = 0);
                                }
                            } else if (connection.autoClickerBStage == 3) {
                                if ((packet instanceof PacketPlayInFlying)) {
                                    connection.autoClickerBStage = 4;
                                } else {
                                    connection.autoClickerBStage = (connection.autoClickerBThreshold = connection.killAuraTypePOther = 0);
                                }
                            } else if (connection.autoClickerBStage == 4) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 1)) {
                                    connection.killAuraTypePOther += 1;
                                    connection.autoClickerBStage = 0;
                                } else {
                                    connection.autoClickerBStage = (connection.autoClickerBThreshold = connection.killAuraTypePOther = 0);
                                }
                            }

                            // KillAura Module N
                            if ((connection.killAuraNStage != 1) && ((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 0)) {
                                connection.killAuraQThreshold = 0;
                            }

                            if (connection.killAuraNStage == 0) {
                                if ((packet instanceof PacketPlayInArmAnimation)) {
                                    connection.killAuraNStage = 1;
                                }
                            } else if (connection.killAuraNStage == 1) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 0)) {
                                    connection.killAuraNStage = 2;
                                } else {
                                    connection.killAuraNStage = 0;
                                }
                            } else if (connection.killAuraNStage == 2) {
                                if ((packet instanceof PacketPlayInFlying)) {
                                    connection.killAuraNStage = 3;
                                } else {
                                    connection.killAuraNStage = 0;
                                }
                            } else if (connection.killAuraNStage == 3) {
                                if (((packet instanceof PacketPlayInBlockDig)) && (((PacketPlayInBlockDig)packet).g() == 1)) {
                                    if (++connection.killAuraQThreshold == 5) {
                                        connection.killAuraQThreshold = 0;

                                        String message = connection.getPlayer().getName() + " is using Kill Aura (Module NX)";
                                        runSync(
                                                new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "N", GuardianEvent.DisplayLevel.HIGH, message)
                                        );
                                    }
                                }
                                connection.killAuraNStage = 0;
                            }
                        }
                    }

                    // KillAura Module X
                    if ((packet instanceof PacketPlayInArmAnimation)) {
                        connection.killAuraXSwing = true;
                    }

                    if ((connection.movesReceived > 20L) && (getVersion() <= 47) && (!connection.killAuraXSwing) && Bukkit.shouldGuardianAct() && ((packet instanceof PacketPlayInUseEntity)) && (((PacketPlayInUseEntity)packet).c() == EnumEntityUseAction.ATTACK)) {
                        String message = connection.getPlayer().getName() + " is using Kill Aura (Module X)";
                        runSync(
                                new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "X", GuardianEvent.DisplayLevel.HIGH, message)
                        );
                    }

                    if (Bukkit.shouldGuardianAct()) {
                        // Criticals Module B
                        if (((packet instanceof PacketPlayInFlying)) && (((PacketPlayInFlying)packet).hasPos)) {
                            if (((PacketPlayInFlying)packet).i()) {
                                connection.criticalBStage = 1;
                                connection.criticalBY = ((PacketPlayInFlying)packet).d();
                            } else if ((connection.criticalBStage == 1) && (((PacketPlayInFlying)packet).d() < connection.criticalBY)) {
                                connection.criticalBStage = 2;
                                connection.criticalBHeight = (connection.criticalBY - ((PacketPlayInFlying)packet).d());
                                connection.criticalBY = ((PacketPlayInFlying)packet).d();
                            } else if ((connection.criticalBStage == 2) && (((PacketPlayInFlying)packet).d() > connection.criticalBY)) {
                                connection.criticalBStage = 3;
                                connection.criticalBY = ((PacketPlayInFlying)packet).d();
                            } else if ((connection.criticalBStage == 3) && (((PacketPlayInFlying)packet).d() < connection.criticalBY)) {
                                connection.criticalBStage = 4;
                                connection.criticalBY = ((PacketPlayInFlying)packet).d();
                            } else {
                                connection.criticalBStage = 0;
                            }
                        } else {
                            if (((packet instanceof PacketPlayInArmAnimation)) && (connection.criticalBStage == 4)) {
                                String message = String.format("%s is using Criticals (Module B) [%.4f]", connection.getPlayer().getName(), connection.criticalBHeight);
                                runSync(
                                        new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.CRITICALS, "B", GuardianEvent.DisplayLevel.HIGH, message)
                                                .addData("height", connection.criticalBHeight)
                                );
                            }

                            connection.criticalBStage = 0;
                        }
                    }

                    if (getVersion() <= 47) {
                        // Bad Packets Module A
                        if (connection.justSentSprint) {
                            if ((packet instanceof PacketPlayInFlying)) {
                                connection.justSentSprint = false;
                            } else if (((packet instanceof PacketPlayInEntityAction)) && ((((PacketPlayInEntityAction)packet).d() == 1) || (((PacketPlayInEntityAction)packet).d() == 2))) {
                                connection.justSentSprint = false;
                            } else {
                                long now = System.currentTimeMillis();
                                if (now - connection.lastSprintViolationTime > 1000L) {
                                    connection.lastSprintViolationTime = now;

                                    String message = connection.getPlayer().getName() + " sent Bad Packets (Module A) [" + packet.getClass().getSimpleName() + "]";
                                    runSync(
                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.GENERAL, "A", GuardianEvent.DisplayLevel.HIGH, message)
                                                    .addData("packet", packet.getClass().getSimpleName())
                                    );
                                }
                                connection.justSentSprint = false;
                            }
                        }
                        if (((packet instanceof PacketPlayInEntityAction)) && ((((PacketPlayInEntityAction)packet).d() == 4) || (((PacketPlayInEntityAction)packet).d() == 5))) {
                            connection.justSentSprint = true;
                        }
                    }
                }
                // Guardian end
                this.k.add(packet);
            }
        }
    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        i.debug("Set listener of {} to {}", new Object[] { this, packetlistener});
        this.o = packetlistener;
    }

    public void handle(Packet packet, GenericFutureListener... agenericfuturelistener) {
        if (this.m != null && this.m.isOpen()) {
            // this.i(); // MineHQ
            this.b(packet, agenericfuturelistener);
        } else {
            // this.l.add(new QueuedPacket(packet, agenericfuturelistener)); // MineHQ
        }
    }

    private void b(Packet packet, GenericFutureListener[] agenericfuturelistener) {
        EnumProtocol enumprotocol = EnumProtocol.a(packet);
        EnumProtocol enumprotocol1 = (EnumProtocol) this.m.attr(d).get();

        if (enumprotocol1 != enumprotocol) {
            i.debug("Disabled auto read");
            this.m.config().setAutoRead(false);
        }

        if (this.m.eventLoop().inEventLoop()) {
            /* Poweruser - is done in QueuedProtocolSwitch.execute(..)
            if (enumprotocol != enumprotocol1) {
                this.a(enumprotocol);
            }
            */

            QueuedProtocolSwitch.execute(this, enumprotocol, enumprotocol1, packet, agenericfuturelistener); // Poweruser
        } else {
            this.m.eventLoop().execute(new QueuedProtocolSwitch(this, enumprotocol, enumprotocol1, packet, agenericfuturelistener));
        }
    }

    // MineHQ start - remove unneeded packet queue
    /*
    private void i() {
        if (this.m != null && this.m.isOpen()) {
            // PaperSpigot  start - Improve Network Manager packet handling
            QueuedPacket queuedpacket;
            while ((queuedpacket = (QueuedPacket) this.l.poll()) != null) {
                this.b(QueuedPacket.a(queuedpacket), QueuedPacket.b(queuedpacket));
            }
            // PaperSpigot end
        }
    }
    */
    // MineHQ end

    public void a() {
        // this.i(); // MineHQ
        EnumProtocol enumprotocol = (EnumProtocol) this.m.attr(d).get();

        if (this.p != enumprotocol) {
            if (this.p != null) {
                this.o.a(this.p, enumprotocol);
            }

            this.p = enumprotocol;
        }

        if (this.o != null) {
            boolean processed = false; // Guardian
            // PaperSpigot start - Improve Network Manager packet handling - Configurable packets per player per tick processing
            Packet packet;
            for (int i = org.github.paperspigot.PaperSpigotConfig.maxPacketsPerPlayer; (packet = (Packet) this.k.poll()) != null && i >= 0; --i) {
                // PaperSpigot end

                // CraftBukkit start
                if (!this.isConnected() || !this.m.config().isAutoRead()) {
                    continue;
                }
                // CraftBukkit end

                // Poweruser start
                if(this.lockDownIncomingTraffic) {
                    this.k.clear();
                    break;
                }
                // Poweruser end

                // Guardian start
                if (!processed) {
                    this.ticksSinceLastPacket = (MinecraftServer.currentTick - this.lastTickNetworkProcessed);
                    this.lastTickNetworkProcessed = MinecraftServer.currentTick;
                    this.currentTime = System.currentTimeMillis();
                    processed = true;
                }

                if (o instanceof PlayerConnection) {
                    PlayerConnection connection = (PlayerConnection) o;

                    if ((packet instanceof PacketPlayInKeepAlive)) {
                        ((PlayerConnection)this.o).handleKeepAliveSync((PacketPlayInKeepAlive)packet);
                        continue;
                    }

                    if (((packet instanceof PacketPlayInChat)) || ((packet instanceof PacketPlayInCustomPayload))) {
                        packet.handle(this.o);
                        continue;
                    }

                    if (Bukkit.shouldGuardianAct() && !MinecraftServer.getServer().getAllowFlight() && !connection.player.abilities.canFly && !connection.player.abilities.canInstantlyBuild) {
                        boolean eventFired = false;
                        int size = connection.lastPacketsQueue.size();
                        if (size >= this.packets.length) {
                            if (((packet instanceof PacketPlayInUseEntity)) && (((PacketPlayInUseEntity)packet).c() == EnumEntityUseAction.ATTACK)) {
                                for (int j = 0; j < this.packets.length; j++) {
                                    this.packets[j] = connection.lastPacketsQueue.removeLast();
                                }

                                Class packet0Class = this.packets[0].getClass();
                                if (packet0Class.equals(PacketPlayInArmAnimation.class)) {
                                    if ((this.packets[1].getClass().equals(PacketPlayInUseEntity.class)) && (this.packets[2].getClass().equals(PacketPlayInArmAnimation.class)) && (((PacketPlayInUseEntity)this.packets[1]).c() == EnumEntityUseAction.ATTACK) && (getVersion() <= 47)) {
                                        if ((this.packets[3].getClass().equals(PacketPlayInUseEntity.class)) && (this.packets[4].getClass().equals(PacketPlayInArmAnimation.class)) && (((PacketPlayInUseEntity)this.packets[3]).c() == EnumEntityUseAction.ATTACK)) {
                                            if ((this.packets[5].getClass().equals(PacketPlayInUseEntity.class)) && (this.packets[6].getClass().equals(PacketPlayInArmAnimation.class)) && (((PacketPlayInUseEntity)this.packets[5]).c() == EnumEntityUseAction.ATTACK)) {
                                                if ((this.packets[7].getClass().equals(PacketPlayInUseEntity.class)) && (this.packets[8].getClass().equals(PacketPlayInArmAnimation.class)) && (((PacketPlayInUseEntity)this.packets[7]).c() == EnumEntityUseAction.ATTACK)) {
                                                    if (this.lastKillAuraKTick != MinecraftServer.currentTick) {
                                                        String message = connection.getPlayer().getName() + " is using Kill Aura (Module KX) [" + this.ticksSinceLastPacket + "]";
                                                        Bukkit.getPluginManager().callEvent(
                                                                new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "K", GuardianEvent.DisplayLevel.HIGH, message)
                                                                        .addData("ticksSinceLastPacket", ticksSinceLastPacket)
                                                        );

                                                        this.lastKillAuraKTick = MinecraftServer.currentTick;
                                                    }
                                                } else {
                                                    String message = connection.getPlayer().getName() + " is using Kill Aura (Module SX) [" + this.ticksSinceLastPacket + "]";
                                                    Bukkit.getPluginManager().callEvent(
                                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "S", GuardianEvent.DisplayLevel.HIGH, message)
                                                                    .addData("ticksSinceLastPacket", ticksSinceLastPacket)
                                                    );
                                                }
                                            } else {
                                                this.numOfKillAuraTLogs.add(this.ticksSinceLastPacket);
                                                if (++this.numOfT == 3) {
                                                    String message = connection.getPlayer().getName() + " is using Kill Aura (Module T) [" + Joiner.on(" ").join(this.numOfKillAuraTLogs) + "]";
                                                    Bukkit.getPluginManager().callEvent(
                                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "T", GuardianEvent.DisplayLevel.HIGH, message)
                                                                    .addData("logs", Joiner.on(" ").join(this.numOfKillAuraTLogs))
                                                    );

                                                    this.numOfT = 0;
                                                    this.numOfKillAuraTLogs.clear();
                                                }
                                            }
                                        } else {
                                            this.numOfKillAuraBLogs.add(this.ticksSinceLastPacket);
                                            if (++this.numOfKillAuraB == 5) {
                                                String message = connection.getPlayer().getName() + " is using Kill Aura (Module B) [" + Joiner.on(" ").join(this.numOfKillAuraBLogs) + "]";
                                                Bukkit.getPluginManager().callEvent(
                                                        new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "B", GuardianEvent.DisplayLevel.HIGH, message)
                                                                .addData("logs", Joiner.on(" ").join(this.numOfKillAuraBLogs))
                                                );

                                                this.numOfKillAuraB = 0;
                                                this.numOfKillAuraBLogs.clear();
                                            }
                                        }
                                    } else {
                                        int numberOfPreviousPacketPlayInPositionLooks = 0;
                                        boolean foundPrevHitPacket = false;
                                        for (int j = 1; j < this.packets.length; j++) {
                                            Class packetJClass = this.packets[j].getClass();
                                            if ((packetJClass.equals(PacketPlayInPositionLook.class)) || (packetJClass.equals(PacketPlayInPosition.class)) || (packetJClass.equals(PacketPlayInLook.class)) || (packetJClass.equals(PacketPlayInFlying.class))) {
                                                numberOfPreviousPacketPlayInPositionLooks++;
                                                foundPrevHitPacket = true;
                                            } else {
                                                if (packetJClass.equals(PacketPlayInUseEntity.class)) {
                                                    break;
                                                }
                                            }
                                        }

                                        if (foundPrevHitPacket) {
                                            PacketPlayInUseEntity packetPlayInUseEntity = (PacketPlayInUseEntity)packet;
                                            EnumEntityUseAction action = packetPlayInUseEntity.c();
                                            WorldServer worldserver = MinecraftServer.getServer().getWorldServer(connection.player.dimension);
                                            Entity entity = packetPlayInUseEntity.a(worldserver);

                                            boolean validData;
                                            validData = (action == EnumEntityUseAction.ATTACK) && (entity != connection.player) && (entity != null) && ((entity instanceof EntityPlayer));

                                            if (validData) {
                                                EntityPlayer entityPlayer = (EntityPlayer)entity;
                                                validData = entityPlayer.playerConnection.hasMovedInHalfSecond;
                                            }

                                            if (validData) {
                                                List<Long> times = connection.killAuraFViolations.get(numberOfPreviousPacketPlayInPositionLooks);

                                                if (connection.killAuraFViolations.size() > 1) {
                                                    connection.killAuraFViolations.clear();
                                                }

                                                if (times == null) {
                                                    times = new ArrayList<Long>();
                                                    connection.killAuraFViolations.put(numberOfPreviousPacketPlayInPositionLooks, times);
                                                }

                                                long currentTime = System.currentTimeMillis();
                                                int typeCViolations = 0;
                                                times.add(currentTime);

                                                for (Iterator<Long> iterator = times.iterator(); iterator.hasNext();) {
                                                    Long time = iterator.next();
                                                    long timeLimiter = numberOfPreviousPacketPlayInPositionLooks < 12 ? this.limitTimes[numberOfPreviousPacketPlayInPositionLooks] : 4000L;

                                                    if (time + timeLimiter >= currentTime) {
                                                        typeCViolations++;
                                                    } else {
                                                        iterator.remove();
                                                    }
                                                }

                                                if (typeCViolations >= 10) {
                                                    String message = connection.getPlayer().getName() + " is using Kill Aura (Module FX) [" + numberOfPreviousPacketPlayInPositionLooks + "]";
                                                    Bukkit.getPluginManager().callEvent(
                                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "F", GuardianEvent.DisplayLevel.HIGH, message)
                                                                    .addData("packets", numberOfPreviousPacketPlayInPositionLooks)
                                                    );

                                                    times.clear();
                                                    eventFired = true;
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int j = this.packets.length - 1; j >= 0; j--) {
                                    connection.lastPacketsQueue.add(this.packets[j]);
                                }
                            } else if ((packet instanceof PacketPlayInBlockPlace)) {
                                for (int j = 0; j < 3; j++) {
                                    this.packets[j] = connection.lastPacketsQueue.removeLast();
                                }

                                if (((this.packets[0] instanceof PacketPlayInFlying)) && ((this.packets[1] instanceof PacketPlayInBlockPlace)) && ((this.packets[2] instanceof PacketPlayInBlockDig))) {
                                    String message = connection.getPlayer().getName() + " is eating or shooting too fast (Module A)";
                                    Bukkit.getPluginManager().callEvent(
                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.FAST_USE, "A", GuardianEvent.DisplayLevel.HIGH, message)
                                    );
                                }

                                for (int j = 2; j >= 0; j--) {
                                    connection.lastPacketsQueue.add(this.packets[j]);
                                }
                            } else if ((packet instanceof PacketPlayInHeldItemSlot)) {
                                for (int j = 0; j < 3; j++) {
                                    this.packets[j] = connection.lastPacketsQueue.removeLast();
                                }
                                if (((this.packets[0] instanceof PacketPlayInBlockPlace)) && ((this.packets[1] instanceof PacketPlayInFlying)) && ((this.packets[2] instanceof PacketPlayInBlockDig))) {
                                    String message = connection.getPlayer().getName() + " is eating or shooting too fast (Module B)";
                                    Bukkit.getPluginManager().callEvent(
                                            new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.FAST_USE, "B", GuardianEvent.DisplayLevel.HIGH, message)
                                    );
                                }
                                for (int j = 2; j >= 0; j--) {
                                    connection.lastPacketsQueue.add(this.packets[j]);
                                }
                            } if (connection.isDigging) {
                                if ((packet instanceof PacketPlayInFlying)) {
                                    connection.killAuraRStage = 1;
                                } else if ((packet instanceof PacketPlayInArmAnimation)) {
                                    if (connection.killAuraRStage == 1) {
                                        connection.killAuraRStage = 2;
                                    } else if (connection.killAuraRStage == 2) {
                                        connection.killAuraRStage = 0;

                                        if (connection.digHorizontalMovement > 1) {
                                            connection.killAuraRThreshold += 1;

                                            if (connection.killAuraRThreshold >= 3) {
                                                String message = connection.getPlayer().getName() + " is using Kill Aura (Module RX) [" + connection.killAuraRThreshold + "]";
                                                Bukkit.getPluginManager().callEvent(
                                                        new GuardianEvent(connection.getPlayer(), GuardianEvent.Cheat.KILL_AURA, "R", GuardianEvent.DisplayLevel.HIGH, message)
                                                                .addData("threshold", connection.killAuraRThreshold)
                                                );
                                            }
                                        }
                                    }
                                } else {
                                    connection.killAuraRStage = 0;
                                }
                            } else {
                                connection.killAuraRStage = 0;
                            }
                        }

                        if (size == 10) {
                            connection.lastPacketsQueue.removeFirst();
                        }

                        connection.lastPacketsQueue.add(packet);

                        if (eventFired) {
                            connection.lastPacketsQueue.clear();
                        }
                    }
                }
                // Guardian end

                // Poweruser start
                CustomTimingsHandler packetHandlerTimer = SpigotTimings.getPacketHandlerTimings(packet);
                packetHandlerTimer.startTiming();
                try {
                    packet.handle(this.o);
                } finally {
                    packetHandlerTimer.stopTiming();
                }
                // Poweruser end
            }

            this.o.a();
        }

        this.m.flush();
    }

    // Guardian start
    private void runSync(final GuardianEvent event) {
        MinecraftServer.getServer().processQueue.add(new Runnable() {

            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }

        });
    }
    // Guardian end

    public SocketAddress getSocketAddress() {
        return this.n;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        // Spigot Start
        this.preparing = false;
        this.k.clear(); // Spigot Update - 20140921a
        // this.l.clear(); // Spigot Update - 20140921a // MineHQ
        // Spigot End
        if (this.m.isOpen()) {
            this.m.close();
            this.q = ichatbasecomponent;
        }
    }

    public boolean c() {
        return this.m instanceof LocalChannel || this.m instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey) {
        this.m.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.m.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
        this.r = true;
    }

    public boolean isConnected() {
        return this.m != null && this.m.isOpen();
    }

    public PacketListener getPacketListener() {
        return this.o;
    }

    public IChatBaseComponent f() {
        return this.q;
    }

    public void g() {
        this.m.config().setAutoRead(false);
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Object object) {
        this.a(channelhandlercontext, (Packet) object);
    }

    static Channel a(NetworkManager networkmanager) {
        return networkmanager.m;
    }

    // Spigot Start
    public SocketAddress getRawAddress()
    {
        return this.m.remoteAddress();
    }
    // Spigot End


    // Spigot start - protocol patch
    public void enableCompression() {
        // Fix ProtocolLib compatibility
        if ( m.pipeline().get("protocol_lib_decoder") != null ) {
            m.pipeline().addBefore( "protocol_lib_decoder", "decompress", new SpigotDecompressor() );
        } else {
            m.pipeline().addBefore( "decoder", "decompress", new SpigotDecompressor() );
        }

        m.pipeline().addBefore( "encoder", "compress", new SpigotCompressor() );
    }
    // Spigot end
}
