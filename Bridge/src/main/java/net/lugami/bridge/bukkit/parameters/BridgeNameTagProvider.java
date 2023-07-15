package net.lugami.bridge.bukkit.parameters;

import net.lugami.qlib.nametag.NametagInfo;
import net.lugami.qlib.nametag.NametagProvider;
import net.lugami.bridge.bukkit.BukkitAPI;
import org.bukkit.entity.Player;

public class BridgeNameTagProvider extends NametagProvider {

    public BridgeNameTagProvider() {
        super("Bridge Provider", 1);
    }

    @Override
    public NametagInfo fetchNametag(Player player, Player viewer) {
        return createNametag(BukkitAPI.getColor(player), "");
    }
}
