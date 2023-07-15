package com.cheatbreaker.nethandler.client;

import com.cheatbreaker.nethandler.ByteBufWrapper;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.server.ICBNetHandlerServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketClientVoice extends CBPacket {

    private byte[] data;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        this.writeBlob(out, this.data);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.data = this.readBlob(in);
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerServer)handler).handleVoice(this);
    }

}
