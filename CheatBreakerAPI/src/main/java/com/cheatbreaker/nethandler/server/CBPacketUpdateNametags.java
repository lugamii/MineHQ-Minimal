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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketUpdateNametags extends CBPacket {

    private Map<UUID, List<String>> playersMap;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeVarInt(this.playersMap == null ? -1 : this.playersMap.size());
        if (this.playersMap != null) {
            for (Map.Entry<UUID, List<String>> entry : this.playersMap.entrySet()) {
                UUID uuid = entry.getKey();
                List<String> tags = entry.getValue();
                out.writeUUID(uuid);
                out.writeVarInt(tags.size());
                tags.forEach(out::writeString);
            }
        }
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        int playersMapSize = in.readVarInt();
        if (playersMapSize == -1) {
            this.playersMap = null;
        } else {
            this.playersMap = new HashMap<>();
            for (int i = 0; i < playersMapSize; ++i) {
                UUID uuid = in.readUUID();
                int tagsSize = in.readVarInt();
                ArrayList<String> tags = new ArrayList<String>();
                for (int j = 0; j < tagsSize; ++j) {
                    tags.add(in.readString());
                }
                this.playersMap.put(uuid, tags);
            }
        }
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleNametagsUpdate(this);
    }

}
