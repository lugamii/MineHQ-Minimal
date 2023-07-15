package net.lugami.bridge.global.packets;

import com.google.gson.JsonObject;
import java.util.UUID;

import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.util.JsonChain;

public class RankCreatePacket
implements Packet {
    private UUID rank;
    private String creator;
    private String server;

    public RankCreatePacket() {
    }

    public RankCreatePacket(UUID rank, String creator, String server) {
        this.rank = rank;
        this.creator = creator;
        this.server = server;
    }

    @Override
    public void onReceive() {

    }

    public int id() {
        return 0;
    }

    public String sentFrom() {
        return this.server;
    }

    public boolean selfRecieve() {
        return false;
    }

    public JsonObject serialize() {
        return new JsonChain().addProperty("rank", this.rank.toString()).addProperty("creator", this.creator).addProperty("server", this.server).get();
    }

    public void deserialize(JsonObject jsonObject) {
        this.rank = UUID.fromString(jsonObject.get("rank").getAsString());
        this.creator = jsonObject.get("creator").getAsString();
        this.server = jsonObject.get("server").getAsString();
    }

    public UUID getRank() {
        return this.rank;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getServer() {
        return this.server;
    }
}

