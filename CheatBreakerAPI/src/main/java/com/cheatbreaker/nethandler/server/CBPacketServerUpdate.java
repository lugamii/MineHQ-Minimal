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
public class CBPacketServerUpdate extends CBPacket {

    private String server;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.server);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.server = in.readString();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleServerUpdate(this);
    }

}
