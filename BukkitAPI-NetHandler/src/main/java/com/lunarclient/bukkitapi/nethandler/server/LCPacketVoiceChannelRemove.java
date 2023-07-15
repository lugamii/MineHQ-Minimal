package com.lunarclient.bukkitapi.nethandler.server;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.client.LCNetHandlerClient;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public final class LCPacketVoiceChannelRemove extends LCPacket {

    @Getter private UUID uuid;

    @Override
    public void write(ByteBufWrapper b) {
        b.writeUUID(uuid);
    }

    @Override
    public void read(ByteBufWrapper b) {
        this.uuid = b.readUUID();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleVoiceChannelDelete(this);
    }
}
