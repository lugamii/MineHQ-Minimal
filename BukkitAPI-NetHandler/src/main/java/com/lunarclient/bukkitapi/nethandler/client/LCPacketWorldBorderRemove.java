package com.lunarclient.bukkitapi.nethandler.client;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;

public final class LCPacketWorldBorderRemove extends LCPacket {

    @Getter private String id;

    public LCPacketWorldBorderRemove() {}

    public LCPacketWorldBorderRemove(String id) {
        this.id = id;
    }

    @Override
    public void write(ByteBufWrapper buf) throws IOException {
        buf.writeString(id);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.id = buf.readString();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerClient) handler).handleWorldBorderRemove(this);
    }

}