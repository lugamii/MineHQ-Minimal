package net.lugami.qlib.util;

import net.lugami.qlib.qLib;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BungeeUtils {

    private BungeeUtils() {
    }

    public static void send(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (IOException ignored) {}
        player.sendPluginMessage(qLib.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void sendAll(String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (IOException ignored) {}
        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
            player.sendPluginMessage(qLib.getInstance(), "BungeeCord", b.toByteArray());
        }
    }
}

