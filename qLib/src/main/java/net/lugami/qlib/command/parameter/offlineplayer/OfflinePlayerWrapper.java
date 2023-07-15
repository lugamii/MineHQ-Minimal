package net.lugami.qlib.command.parameter.offlineplayer;

import java.util.UUID;

import net.lugami.qlib.qLib;
import net.lugami.qlib.visibility.FrozenVisibilityHandler;
import net.lugami.qlib.util.Callback;
import net.lugami.qlib.util.UUIDUtils;
import org.bukkit.command.CommandSender;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class OfflinePlayerWrapper {

    private String source;
    private UUID uniqueId;
    private String name;
    private CommandSender commandSender;

    public OfflinePlayerWrapper(CommandSender commandSender, String source) {
        this.commandSender = commandSender;
        this.source = source;
    }

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }

    public void loadAsync(final Callback<Player> callback) {
        new BukkitRunnable(){

            public void run() {
                final Player player = OfflinePlayerWrapper.this.loadSync();
                new BukkitRunnable(){

                    public void run() {
                        callback.callback(player);
                    }
                }.runTask(qLib.getInstance());
            }

        }.runTaskAsynchronously(qLib.getInstance());
    }

    public Player loadSync() {
        if (!(this.source.charAt(0) != '\"' && this.source.charAt(0) != '\'' || this.source.charAt(this.source.length() - 1) != '\"' && this.source.charAt(this.source.length() - 1) != '\'')) {
            this.source = this.source.replace("'", "").replace("\"", "");
            this.uniqueId = UUIDUtils.uuid(this.source);
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }
            this.name = UUIDUtils.name(this.uniqueId);
            if (Bukkit.getPlayer(this.uniqueId) != null) {
                return Bukkit.getPlayer(this.uniqueId);
            }
            if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
                return null;
            }
            MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
            CraftPlayer player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        }
        if (commandSender != null && playerCheck(commandSender, source) != null) {
            return playerCheck(commandSender, source);
        }

        this.uniqueId = UUIDUtils.uuid(this.source);
        if (this.uniqueId == null) {
            this.name = this.source;
            return null;
        }
        this.name = UUIDUtils.name(this.uniqueId);
        if (Bukkit.getPlayer(this.uniqueId) != null) {
            return Bukkit.getPlayer(this.uniqueId);
        }
        if (!Bukkit.getOfflinePlayer(this.uniqueId).hasPlayedBefore()) {
            return null;
        }
        MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager(server.getWorldServer(0)));
        CraftPlayer player = entity.getBukkitEntity();
        if (player != null) {
            player.loadData();
        }
        return player;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }


    public Player playerCheck(CommandSender sender, String value) {
        Player player = Bukkit.getServer().getPlayer(value);
        if(player == null) {
            return null;
        }

        if(!(sender instanceof Player)) return player;

        if(sender instanceof Player && !FrozenVisibilityHandler.treatAsOnline(player, (Player)sender)) {
            return null;
        }

        if(player.isDisguised()) {
            if(sender == player || sender.hasPermission("basic.staff")) {
                return player;
            }
            if(!player.getDisguisedName().toLowerCase().startsWith(value.toLowerCase())) return null;
        }

        return player;
    }

}

