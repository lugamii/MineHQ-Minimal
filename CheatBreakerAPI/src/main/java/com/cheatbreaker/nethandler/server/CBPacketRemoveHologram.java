package com.cheatbreaker.nethandler.server;

import com.cheatbreaker.nethandler.ByteBufWrapper;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.client.ICBNetHandlerClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketRemoveHologram extends CBPacket {

    private UUID uuid;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.uuid);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.uuid = in.readUUID();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleRemoveHologram(this);
    }

}
