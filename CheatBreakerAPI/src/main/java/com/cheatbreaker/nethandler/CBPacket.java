package com.cheatbreaker.nethandler;

import com.cheatbreaker.nethandler.client.CBPacketClientVoice;
import com.cheatbreaker.nethandler.client.CBPacketVoiceChannelSwitch;
import com.cheatbreaker.nethandler.client.CBPacketVoiceMute;
import com.cheatbreaker.nethandler.server.*;
import com.cheatbreaker.nethandler.shared.CBPacketAddWaypoint;
import com.cheatbreaker.nethandler.shared.CBPacketRemoveWaypoint;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import net.minecraft.util.io.netty.buffer.Unpooled;

import java.io.IOException;

public abstract class CBPacket {

    private static final BiMap<Class, Integer> REGISTRY = HashBiMap.create();

    @Getter private Object attachment;

    public abstract void write(ByteBufWrapper var1) throws IOException;

    public abstract void read(ByteBufWrapper var1) throws IOException;

    public abstract void process(ICBNetHandler var1);

    public static CBPacket handle(ICBNetHandler netHandler, byte[] data) {
        return CBPacket.handle(netHandler, data, null);
    }

    public static CBPacket handle(ICBNetHandler netHandler, byte[] data, Object attachment) {
        ByteBufWrapper wrappedBuffer = new ByteBufWrapper(Unpooled.wrappedBuffer(data));
        int packetId = wrappedBuffer.readVarInt();
        Class packetClass = REGISTRY.inverse().get(packetId);
        if (packetClass != null) {
            try {
                CBPacket packet = (CBPacket)packetClass.newInstance();
                if (attachment != null) {
                    packet.attach(attachment);
                }
                packet.read(wrappedBuffer);
                return packet;
            }
            catch (IOException | IllegalAccessException | InstantiationException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] getPacketData(CBPacket packet) {
        ByteBufWrapper wrappedBuffer = new ByteBufWrapper(Unpooled.buffer());
        wrappedBuffer.writeVarInt(REGISTRY.get(packet.getClass()));
        try {
            packet.write(wrappedBuffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return wrappedBuffer.buf().array();
    }

    private static void addPacket(int id, Class clazz) {
        if (REGISTRY.containsKey(clazz)) {
            throw new IllegalArgumentException("Duplicate packet class (" + clazz.getSimpleName() + "), already used by " + REGISTRY.get(clazz));
        }
        if (REGISTRY.containsValue(id)) {
            throw new IllegalArgumentException("Duplicate packet ID (" + id + "), already used by " + REGISTRY.inverse().get(id));
        }
        REGISTRY.put(clazz, id);
    }

    protected void writeBlob(ByteBufWrapper b, byte[] bytes) {
        b.buf().writeShort(bytes.length);
        b.buf().writeBytes(bytes);
    }

    protected byte[] readBlob(ByteBufWrapper b) {
        short key = b.buf().readShort();
        if (key < 0) {
            System.out.println("Key was smaller than nothing!  Weird key!");
            return null;
        }
        byte[] blob = new byte[key];
        b.buf().readBytes(blob);
        return blob;
    }

    public void attach(Object obj) {
        this.attachment = obj;
    }

    static {
        CBPacket.addPacket(0, CBPacketAddWaypoint.class);
        CBPacket.addPacket(2, CBPacketRemoveWaypoint.class);
        CBPacket.addPacket(3, CBPacketCooldown.class);
        CBPacket.addPacket(4, CBPacketNotification.class);
        CBPacket.addPacket(5, CBPacketStaffModState.class);
        CBPacket.addPacket(6, CBPacketUpdateNametags.class);
        CBPacket.addPacket(7, CBPacketTeammates.class);
        CBPacket.addPacket(8, CBPacketOverrideNametags.class);
        CBPacket.addPacket(9, CBPacketAddHologram.class);
        CBPacket.addPacket(10, CBPacketUpdateHologram.class);
        CBPacket.addPacket(11, CBPacketRemoveHologram.class);
        CBPacket.addPacket(12, CBPacketTitle.class);
        CBPacket.addPacket(14, CBPacketServerRule.class);
        CBPacket.addPacket(15, CBPacketClientVoice.class);
        CBPacket.addPacket(16, CBPacketVoice.class);
        CBPacket.addPacket(17, CBPacketVoiceChannel.class);
        CBPacket.addPacket(18, CBPacketVoiceChannelUpdate.class);
        CBPacket.addPacket(19, CBPacketVoiceChannelSwitch.class);
        CBPacket.addPacket(20, CBPacketVoiceMute.class);
        CBPacket.addPacket(21, CBPacketDeleteVoiceChannel.class);
        CBPacket.addPacket(23, CBPacketUpdateWorld.class);
        CBPacket.addPacket(24, CBPacketServerUpdate.class);
        CBPacket.addPacket(25, CBPacketWorldBorder.class);
        CBPacket.addPacket(26, CBPacketWorldBorderUpdate.class);
        CBPacket.addPacket(27, CBPacketWorldBorderRemove.class);
    }

}
