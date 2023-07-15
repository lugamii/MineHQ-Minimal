package com.cheatbreaker.nethandler.server;

import com.cheatbreaker.nethandler.ByteBufWrapper;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.client.ICBNetHandlerClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketWorldBorderRemove extends CBPacket {

    private String id;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.id);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.id = in.readString();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleWorldBorderRemove(this);
    }

}
