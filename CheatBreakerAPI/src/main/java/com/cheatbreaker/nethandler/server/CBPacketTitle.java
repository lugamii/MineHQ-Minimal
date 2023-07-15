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
public class CBPacketTitle extends CBPacket {

    private String type;
    private String message;
    private float scale;
    private long displayTimeMs;
    private long fadeInTimeMs;
    private long fadeOutTimeMs;

    public CBPacketTitle(String type, String message, long displayTimeMs, long fadeInTimeMs, long fadeOutTimeMs) {
        this(type, message, 1.0f, displayTimeMs, fadeInTimeMs, fadeOutTimeMs);
    }

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeString(this.type);
        out.writeString(this.message);
        out.buf().writeFloat(this.scale);
        out.buf().writeLong(this.displayTimeMs);
        out.buf().writeLong(this.fadeInTimeMs);
        out.buf().writeLong(this.fadeOutTimeMs);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.type = in.readString();
        this.message = in.readString();
        this.scale = in.buf().readFloat();
        this.displayTimeMs = in.buf().readLong();
        this.fadeInTimeMs = in.buf().readLong();
        this.fadeOutTimeMs = in.buf().readLong();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleTitle(this);
    }

}
