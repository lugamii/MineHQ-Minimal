package com.lunarclient.bukkitapi.nethandler.client;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.server.LCNetHandlerServer;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;

public final class LCPacketClientVoice extends LCPacket {

    @Getter private byte[] data;

    public LCPacketClientVoice() {
    }

    public LCPacketClientVoice(byte[] data) {
        this.data = data;
    }

    @Override
    public void write(ByteBufWrapper b) throws IOException {
        writeBlob(b, this.data);
    }

    @Override
    public void read(ByteBufWrapper b) throws IOException {
        this.data = readBlob(b);
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerServer) handler).handleVoice(this);
    }
}
