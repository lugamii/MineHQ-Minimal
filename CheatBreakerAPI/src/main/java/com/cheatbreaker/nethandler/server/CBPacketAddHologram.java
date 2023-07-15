package com.cheatbreaker.nethandler.server;

import com.cheatbreaker.nethandler.ByteBufWrapper;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.client.ICBNetHandlerClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketAddHologram extends CBPacket {

    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private List<String> lines;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.uuid);
        out.buf().writeDouble(this.x);
        out.buf().writeDouble(this.y);
        out.buf().writeDouble(this.z);
        out.writeVarInt(this.lines.size());
        this.lines.forEach(out::writeString);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.uuid = in.readUUID();
        this.x = in.buf().readDouble();
        this.y = in.buf().readDouble();
        this.z = in.buf().readDouble();
        int linesSize = in.readVarInt();
        this.lines = new ArrayList<>();
        for (int i = 0; i < linesSize; ++i) {
            this.lines.add(in.readString());
        }
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleAddHologram(this);
    }

}
