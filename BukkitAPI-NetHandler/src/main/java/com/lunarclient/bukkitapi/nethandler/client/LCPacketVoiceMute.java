package com.lunarclient.bukkitapi.nethandler.client;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.server.LCNetHandlerServer;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

public final class LCPacketVoiceMute extends LCPacket {

    @Getter private UUID muting;

    public LCPacketVoiceMute() {
    }

    public LCPacketVoiceMute(UUID muting) {
        this.muting = muting;
    }

    @Override
    public void write(ByteBufWrapper b) throws IOException {
        b.writeUUID(muting);
    }

    @Override
    public void read(ByteBufWrapper b) throws IOException {
        this.muting = b.readUUID();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerServer) handler).handleVoiceMute(this);
    }
}
