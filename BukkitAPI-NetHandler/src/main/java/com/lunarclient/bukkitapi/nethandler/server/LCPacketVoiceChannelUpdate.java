package com.lunarclient.bukkitapi.nethandler.server;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.client.LCNetHandlerClient;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public final class LCPacketVoiceChannelUpdate extends LCPacket {

    /**
     * 0 for adding a player
     * 1 for removing a player
     * 2 for marking a player as listening
     * 3 for marking a player as deafened
     */
    @Getter public int status;

    @Getter private UUID channelUuid;

    @Getter private UUID uuid;

    @Getter private String name;

    @Override
    public void write(ByteBufWrapper b) {
        b.writeVarInt(status);
        b.writeUUID(channelUuid);
        b.writeUUID(uuid);
        b.writeString(name);
    }

    @Override
    public void read(ByteBufWrapper b) {
        this.status = b.readVarInt();
        this.channelUuid = b.readUUID();
        this.uuid = b.readUUID();
        this.name = b.readString();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleVoiceChannelUpdate(this);
    }
}
