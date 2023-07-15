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
public final class LCPacketVoice extends LCPacket {

    @Getter private UUID uuid;

    @Getter private byte[] data;

    @Override
    public void write(ByteBufWrapper b) {
        b.writeUUID(this.uuid);
        writeBlob(b, this.data);
    }

    @Override
    public void read(ByteBufWrapper b) {
        this.uuid = b.readUUID();
        this.data = readBlob(b);
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleVoice(this);
    }
}
