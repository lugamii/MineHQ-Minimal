package net.lugami.bridge.global.packets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.util.JsonChain;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ServerDeployPacket
    implements Packet {
    private String serverName;
    private List<File> plugins;

    @Override
    public void onReceive() {

    }

    public ServerDeployPacket(String serverName, List<File> plugins) {
        this.serverName = serverName;
        this.plugins = plugins;
    }

    public boolean selfRecieve() {
        return false;
    }

    public JsonObject serialize() {
        return new JsonChain().addProperty("serverName", serverName).addProperty("plugins", new Gson().toJson(plugins)).get();
    }

    public void deserialize(JsonObject jsonObject) {
        this.serverName = jsonObject.get("serverName").getAsString();
        this.plugins = Arrays.asList(new Gson().fromJson(jsonObject.get("plugins").getAsString(), File[].class));
    }

    public String getServerName() {
        return this.serverName;
    }

    public List<File> getPlugins() {
        return this.plugins;
    }
}
