package net.lugami.qlib.packet;

import lombok.SneakyThrows;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

public final class ScoreboardTeamPacketMod {
    private final PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
    private static Field aField;
    private static Field bField;
    private static Field cField;
    private static Field dField;
    private static Field eField;
    private static Field fField;
    private static Field gField;

    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection<String> players, int paramInt) {
        try {
            aField.set(this.packet, name);
            fField.set(this.packet, paramInt);
            if (paramInt == 0 || paramInt == 2) {
                bField.set(this.packet, name);
                cField.set(this.packet, prefix);
                dField.set(this.packet, suffix);
                gField.set(this.packet, 3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (paramInt == 0) {
            this.addAll(players);
        }
    }

    public ScoreboardTeamPacketMod(String name, Collection<String> players, int paramInt) {
        try {
            gField.set(this.packet, 3);
            aField.set(this.packet, name);
            fField.set(this.packet, paramInt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.addAll(players);
    }

    @SneakyThrows
    public void sendToPlayer(Player bukkitPlayer) {
        //System.out.println("ScoreboardTeamPacketMod - " + eField.get(this.packet));
        //System.out.println("Sending to " + bukkitPlayer.getName());

        ((CraftPlayer)bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
    }

    private void addAll(Collection<String> col) {
        if (col == null) {
            return;
        }
        try {
            ((Collection<String>)eField.get(this.packet)).addAll(col);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            aField = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            bField = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            cField = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            dField = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            eField = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            fField = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            gField = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            aField.setAccessible(true);
            bField.setAccessible(true);
            cField.setAccessible(true);
            dField.setAccessible(true);
            eField.setAccessible(true);
            fField.setAccessible(true);
            gField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

