package net.lugami.qlib.uuid.impl;

import java.util.UUID;

import net.lugami.qlib.qLib;
import net.lugami.qlib.uuid.UUIDCache;

public final class BukkitUUIDCache implements UUIDCache {

    @Override
    public UUID uuid(String name) {
        return qLib.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
    }

    @Override
    public String name(UUID uuid) {
        return qLib.getInstance().getServer().getOfflinePlayer(uuid).getName();
    }

    @Override
    public void ensure(UUID uuid) {
    }

    @Override
    public void update(UUID uuid, String name) {
    }
}

