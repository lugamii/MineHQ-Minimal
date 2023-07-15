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
public class CBPacketCooldown extends CBPacket {

    private String message;
    private long durationMs;
    private int iconId;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.message);
        out.buf().writeLong(this.durationMs);
        out.buf().writeInt(this.iconId);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.message = in.readString();
        this.durationMs = in.buf().readLong();
        this.iconId = in.buf().readInt();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleCooldown(this);
    }

}
