package net.lugami.qlib.npc;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class NPC {

    @Getter private static List<NPC> npcs = new ArrayList<>();

    private String displayName;
    private UUID uuid;
    private String name, skin;
    private Location location;
    private List<UUID> viewers;

    public NPC(String displayName, UUID uuid, String name, String skin, Location location) {
        this.displayName = displayName;
        this.uuid = uuid;
        this.name = name;
        this.skin = skin;
        this.location = location;
        this.viewers = new ArrayList<>();
        npcs.add(this);
    }

    public NPC(String displayName, UUID uuid, String name, String skin, List<UUID> viewers) {
        this.displayName = displayName;
        this.uuid = uuid;
        this.name = name;
        this.skin = skin;
        this.viewers = viewers;
        npcs.add(this);
    }

    public void send() {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(uuid, name), new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if (viewers.isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            });
        }
    }

}
