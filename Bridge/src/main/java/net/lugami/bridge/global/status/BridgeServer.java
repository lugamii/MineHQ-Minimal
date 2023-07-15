
package net.lugami.bridge.global.status;

import com.google.gson.JsonObject;
import net.lugami.bridge.BridgeGlobal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.WordUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
public class BridgeServer {

    private String name, provider, group, status, motd = "", systemType;
    public String screenName;
    private long lastHeartbeat, bootTime;
    private int online, maximum;
    private double tps;
    private List<String> users;
    private JsonObject metadata;

    public BridgeServer(String name, String provider, String group, String status, String motd, String systemType, long lastHeartbeat, long bootTime, int online, int maximum, double tps, List<String> users) {
        this.name = name;
        this.provider = provider;
        this.group = group;
        this.status = status;
        this.motd = motd;
        this.systemType = systemType;
        this.lastHeartbeat = lastHeartbeat;
        this.bootTime = bootTime;
        this.online = online;
        this.maximum = maximum;
        this.tps = tps;
        this.users = users;
    }

    public BridgeServer(String name, String provider, String group, String status, String systemType, long lastHeartbeat, long bootTime, int online, int maximum, double tps, List<String> users) {
        this.name = name;
        this.provider = provider;
        this.group = group;
        this.status = status;
        this.systemType = systemType;
        this.lastHeartbeat = lastHeartbeat;
        this.bootTime = bootTime;
        this.online = online;
        this.maximum = maximum;
        this.tps = tps;
        this.users = users;
    }

    public boolean isOnline() {
        return System.currentTimeMillis() - lastHeartbeat <= TimeUnit.SECONDS.toMillis(5);
    }

    public boolean isWhitelisted() {
        return status.equalsIgnoreCase("WHITELISTED");
    }

    public String formattedStatus(boolean color) {
        switch(status.toLowerCase()) {
            case "online": {
                return (color ? "§a" : "") + "Online";
            }
            case "whitelisted": {
                return (color ? "§f" : "") + "Whitelisted";
            }
            case "offline": {
                return (color ? "§c" : "") + "Offline";
            }
            case "booting": {
                return (color ? "§6" : "" + "Booting");
            }
            default: {
                return (color ? "§9" : "") + WordUtils.capitalize(status.toLowerCase());
            }
        }
    }

    public StatusProvider getStatusProvider() {
        return BridgeGlobal.getServerHandler().getProvider();
    }

}