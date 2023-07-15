package com.cheatbreaker.nethandler.server;

import com.cheatbreaker.nethandler.ByteBufWrapper;
import com.cheatbreaker.nethandler.CBPacket;
import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.client.ICBNetHandlerClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CBPacketVoiceChannel extends CBPacket {

    private UUID uuid;
    private String name;
    private Map<UUID, String> players;
    private Map<UUID, String> listening;

    @Override
    public void write(ByteBufWrapper out) throws IOException {
        out.writeUUID(this.uuid);
        out.writeString(this.name);
        this.writeMap(out, this.players);
        this.writeMap(out, this.listening);
    }

    @Override
    public void read(ByteBufWrapper in) throws IOException {
        this.uuid = in.readUUID();
        this.name = in.readString();
        this.players = this.readMap(in);
        this.listening = this.readMap(in);
    }

    private void writeMap(ByteBufWrapper out, Map<UUID, String> players) {
        out.writeVarInt(players.size());
        players.forEach((key, value) -> {
            out.writeUUID(key);
            out.writeString(value);
        });
    }

    private Map<UUID, String> readMap(ByteBufWrapper in) {
        int size = in.readVarInt();
        HashMap<UUID, String> players = new HashMap<>();
        for (int i = 0; i < size; ++i) {
            UUID uuid = in.readUUID();
            String name = in.readString();
            players.put(uuid, name);
        }
        return players;
    }

    @Override
    public void process(ICBNetHandler handler) {
        ((ICBNetHandlerClient)handler).handleVoiceChannels(this);
    }

}
