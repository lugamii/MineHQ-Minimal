package net.lugami.bridge.global.packets;

import com.google.gson.JsonObject;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.util.JsonChain;

public class PunishmentPacket
implements Packet {
    private String json;

    public PunishmentPacket() {
    }

    public int id() {
        return 5;
    }

    public String sentFrom() {
        return BridgeGlobal.getSystemName();
    }

    public boolean selfRecieve() {
        return true;
    }

    public JsonObject serialize() {
        return new JsonChain().addProperty("json", this.json).get();
    }

    public void deserialize(JsonObject jsonObject) {
        this.json = jsonObject.get("json").getAsString();
    }

    public String getJson() {
        return this.json;
    }

    public PunishmentPacket(String json) {
        this.json = json;
    }

    @Override
    public void onReceive() {

    }
}

