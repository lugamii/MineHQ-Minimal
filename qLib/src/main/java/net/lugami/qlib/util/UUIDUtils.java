package net.lugami.qlib.util;

import com.mongodb.BasicDBList;
import net.lugami.qlib.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public final class UUIDUtils {

    private UUIDUtils() {
    }

    public static String name(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }
        String name = FrozenUUIDCache.name(uuid);
        return name == null ? "null" : name;
    }

    public static UUID uuid(String name) {
        Player player = Bukkit.getPlayer(name);
        if(player != null) return player.getUniqueId();
        return FrozenUUIDCache.uuid(name);
    }

    public static String formatPretty(UUID uuid) {
        return UUIDUtils.name(uuid) + " [" + uuid + "]";
    }

    public static BasicDBList uuidsToStrings(Collection<UUID> toConvert) {
        if (toConvert == null || toConvert.isEmpty()) {
            return new BasicDBList();
        }
        BasicDBList dbList = new BasicDBList();
        for (UUID uuid : toConvert) {
            dbList.add(uuid.toString());
        }
        return dbList;
    }
}

