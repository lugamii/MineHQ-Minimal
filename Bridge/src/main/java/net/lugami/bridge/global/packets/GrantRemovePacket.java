package net.lugami.bridge.global.packets;

import com.google.gson.JsonObject;
import java.util.UUID;

import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.util.JsonChain;

public class GrantRemovePacket
implements Packet {
    private Grant grant;
    private UUID target;
    private String granter;
    private String server;

    public GrantRemovePacket() {
    }

    public GrantRemovePacket(Grant grant, UUID target, String remover, String server) {
        this.grant = grant;
        this.target = target;
        this.granter = this.granter;
        this.server = server;
    }

    public int id() {
        return 911;
    }

    public String sentFrom() {
        return this.server;
    }

    public boolean selfRecieve() {
        return false;
    }

    public JsonObject serialize() {
        return new JsonChain().addProperty("grant", Grant.serialize(this.grant).toString()).addProperty("target", this.target.toString()).addProperty("granter", this.granter).addProperty("server", this.server).get();
    }

    public void deserialize(JsonObject jsonObject) {
        this.grant = Grant.deserialize(jsonObject.get("grant").getAsString());
        this.target = UUID.fromString(jsonObject.get("target").getAsString());
        this.granter = jsonObject.get("granter").getAsString();
        this.server = jsonObject.get("server").getAsString();
    }

    public Grant getGrant() {
        return this.grant;
    }

    public UUID getTarget() {
        return this.target;
    }

    public String getGranter() {
        return this.granter;
    }

    public String getServer() {
        return this.server;
    }

    @Override
    public void onReceive() {

    }
}

