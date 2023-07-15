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
public class CBPacketWorldBorder extends CBPacket {

    private String id;
    private String world;
    private boolean cancelsExit;
    private boolean canShrinkExpand;
    private int color;
    private double minX;
    private double minZ;
    private double maxX;
    private double maxZ;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeOptional(this.id, out::writeString);
        out.writeString(this.world);
        out.buf().writeBoolean(this.cancelsExit);
        out.buf().writeBoolean(this.canShrinkExpand);
        out.buf().writeInt(this.color);
        out.buf().writeDouble(this.minX);
        out.buf().writeDouble(this.minZ);
        out.buf().writeDouble(this.maxX);
        out.buf().writeDouble(this.maxZ);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.id = in.readOptional(in::readString);
        this.world = in.readString();
        this.cancelsExit = in.buf().readBoolean();
        this.canShrinkExpand = in.buf().readBoolean();
        this.color = in.buf().readInt();
        this.minX = in.buf().readDouble();
        this.minZ = in.buf().readDouble();
        this.maxX = in.buf().readDouble();
        this.maxZ = in.buf().readDouble();
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleWorldBorder(this);
    }

}
