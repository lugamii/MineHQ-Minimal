package net.lugami.bridge.bukkit.commands.server;

import lombok.Getter;
import net.lugami.bridge.bukkit.Bridge;

public class BanLobby {

    @Getter
    private boolean maintenance, restricted, banLobbyEnabled;

    public BanLobby() {
        this.banLobbyEnabled = Bridge.getInstance().getConfig().getBoolean("server.ban-lobby");
    }

    //TODO: Do this shit later

}

