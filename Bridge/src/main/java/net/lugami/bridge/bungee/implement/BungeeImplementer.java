package net.lugami.bridge.bungee.implement;

import com.google.gson.JsonObject;
import net.lugami.bridge.global.status.StatusProvider;
import net.lugami.bridge.BridgeGlobal;
import net.md_5.bungee.BungeeCord;

import java.util.List;
import java.util.stream.Collectors;

public class BungeeImplementer extends StatusProvider {

    public BungeeImplementer() {
        super("BungeeCord", 10);
    }

    @Override
    public String serverName() {
        return BridgeGlobal.getSystemName();
    }

    @Override
    public String serverStatus() {
        return "ONLINE";
    }

    @Override
    public int online() {
        return BungeeCord.getInstance().getOnlineCount();
    }

    @Override
    public int maximum() {
        return BungeeCord.getInstance().getConfig().getPlayerLimit();
    }

    @Override
    public String motd() {
        return "";
    }

    @Override
    public double tps() {
        return 0;
    }

    @Override
    public List<String> players() {
        return BungeeCord.getInstance().getPlayers().stream().map(proxiedPlayer -> proxiedPlayer.getUniqueId().toString()).collect(Collectors.toList());
    }

    @Override
    public JsonObject dataPassthrough() {
        return null;
    }
}
