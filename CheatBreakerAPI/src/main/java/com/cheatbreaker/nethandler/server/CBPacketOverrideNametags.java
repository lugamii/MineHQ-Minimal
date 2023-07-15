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
public class CBPacketOverrideNametags extends CBPacket {

    private UUID player;
    private List<String> tags;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.player);
        out.writeOptional(this.tags, t -> {
            out.writeVarInt(t.size());
            t.forEach(out::writeString);
        });
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.player = in.readUUID();
        this.tags = in.readOptional(() -> {
            int tagsSize = in.readVarInt();
            ArrayList<String> tags = new ArrayList<>();
            for (int i = 0; i < tagsSize; ++i) {
                tags.add(in.readString());
            }
            return tags;
        });
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleOverrideNametags(this);
    }

}
