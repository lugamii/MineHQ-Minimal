package net.lugami.bridge.bukkit.parameters;

import net.lugami.qlib.uuid.UUIDCache;
import net.lugami.bridge.bukkit.BukkitAPI;

import java.util.UUID;

public class BridgeUUIDCache implements UUIDCache {

    @Override
    public UUID uuid(String var1) {
        return BukkitAPI.getProfile(var1).getUuid();
    }

    @Override
    public String name(UUID var1) {
        return BukkitAPI.getName(BukkitAPI.getProfile(var1), false);
    }

    @Override
    public void ensure(UUID var1) {

    }

    @Override
    public void update(UUID var1, String var2) {

    }
}
