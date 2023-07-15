package net.lugami.qlib.tab;

import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PlayerInfoPacketMod {

    private final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

    public PlayerInfoPacketMod(String name, int ping, GameProfile profile, int action) {
        this.setField("username", name);
        this.setField("ping", ping);
        this.setField("action", action);
        this.setField("player", profile);
    }

    public void setField(String field, Object value) {
        try {
            Field fieldObject = this.packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(this.packet, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToPlayer(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(this.packet);
    }
}

