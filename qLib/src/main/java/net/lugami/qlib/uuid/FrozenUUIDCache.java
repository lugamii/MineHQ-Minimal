package net.lugami.qlib.uuid;

import com.google.common.base.Preconditions;
import java.util.UUID;

import net.lugami.qlib.qLib;
import lombok.Getter;

public final class FrozenUUIDCache {

    @Getter private static UUIDCache impl = null;
    private static boolean initiated = false;

    private FrozenUUIDCache() {
    }

    public static void init() {
        Preconditions.checkState(!initiated);
        initiated = true;
        try {
            impl = (UUIDCache)Class.forName(qLib.getInstance().getConfig().getString("UUIDCache.Backend", "Cache")).newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        qLib.getInstance().getServer().getPluginManager().registerEvents(new UUIDListener(), qLib.getInstance());
    }

    public static UUID uuid(String name) {
        return impl.uuid(name);
    }

    public static String name(UUID uuid) {
        return impl.name(uuid);
    }

    public static void ensure(UUID uuid) {
        impl.ensure(uuid);
    }

    public static void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }

}

