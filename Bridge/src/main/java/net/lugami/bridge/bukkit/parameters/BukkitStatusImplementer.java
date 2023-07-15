package net.lugami.bridge.bukkit.parameters;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lugami.bridge.global.status.StatusProvider;
import net.lugami.qlib.uuid.FrozenUUIDCache;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import net.lugami.bridge.global.util.JsonChain;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class BukkitStatusImplementer extends StatusProvider {

    public BukkitStatusImplementer() {
        super("Bridge Bukkit Implementer", 1);
    }

    @Override
    public String serverName() {
        return BridgeGlobal.getServerName();
    }

    @Override
    public String serverStatus() {
        return GeneralListener.getServerStatus();
    }

    @Override
    public int online() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public int maximum() {
        return Bukkit.getMaxPlayers();
    }

    @Override
    public String motd() {
        return Bukkit.getMotd();
    }

    @Override
    public double tps() {
        return MinecraftServer.getServer().recentTps[0];
    }

    @Override
    public List<String> players() {
        return Bukkit.getOnlinePlayers().stream().map(player -> player.getUniqueId().toString()).collect(Collectors.toList());
    }

    @Override
    public JsonObject dataPassthrough() {
        JsonChain jsonChain = new JsonChain();
        if(BridgeGlobal.getUpdaterManager().getRunningDirectory().contains("/temp/"))
            jsonChain.addProperty("tempServer", true).addProperty("runningDirectory", BridgeGlobal.getUpdaterManager().getRunningDirectory()).get();
        jsonChain.addProperty("port", Bukkit.getPort()).addProperty("vanished", new Gson().toJson(Bukkit.getOnlinePlayers().stream().filter(player -> player.hasMetadata("invisible")).map(player -> FrozenUUIDCache.name(player.getUniqueId())).collect(Collectors.toList())));
        return jsonChain.get();
    }
}