package com.lunarclient.bukkitapi.nethandler.client;

import com.lunarclient.bukkitapi.nethandler.ByteBufWrapper;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import com.lunarclient.bukkitapi.nethandler.server.LCNetHandlerServer;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;
import lombok.Getter;

import java.io.IOException;
import java.util.UUID;

public final class LCPacketVoiceChannelSwitch extends LCPacket {

    @Getter private UUID switchingTo;

    public LCPacketVoiceChannelSwitch() {
    }

    public LCPacketVoiceChannelSwitch(UUID switchingTo) {
        this.switchingTo = switchingTo;
    }

    @Override
    public void write(ByteBufWrapper b) throws IOException {
        b.writeUUID(switchingTo);
    }

    @Override
    public void read(ByteBufWrapper b) throws IOException {
        this.switchingTo = b.readUUID();
    }

    @Override
    public void process(LCNetHandler handler) {
        ((LCNetHandlerServer) handler).handleVoiceChannelSwitch(this);
    }
}
