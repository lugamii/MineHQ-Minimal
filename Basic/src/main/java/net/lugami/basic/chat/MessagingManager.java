package net.lugami.basic.chat;

import net.lugami.qlib.qLib;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessagingManager {

    private final String key = "basic:messageSettings";
    private Set<UUID> globalSpyEnabled = new HashSet<UUID>();
    private Map<UUID, Set<UUID>> playerSpy = new HashMap<UUID, Set<UUID>>();
    private Set<UUID> ignoresInvis = new HashSet<UUID>();

    public UUID getLastMessaged(UUID player) {
        String lastMessaged = qLib.getInstance().runBackboneRedisCommand(redis -> redis.hget("basic:messageSettings:" + player.toString(), "lastMessaged"));
        return lastMessaged == null ? null : UUID.fromString(lastMessaged);
    }

    public void setLastMessaged(UUID player, UUID lastMessaged) {
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.hset("basic:messageSettings:" + player.toString(), "lastMessaged", lastMessaged.toString()));
    }

    public Boolean isMessagesDisabled(UUID player) {
        return qLib.getInstance().runBackboneRedisCommand(redis -> Boolean.valueOf(redis.hget("basic:messageSettings:" + player.toString(), "messagesDisabled")));
    }

    public Boolean toggleMessages(UUID player) {
        Boolean newValue = !this.isMessagesDisabled(player);
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.hset("basic:messageSettings:" + player.toString(), "messagesDisabled", newValue.toString()));
        return newValue;
    }

    public Boolean isSoundsDisabled(UUID player) {
        return qLib.getInstance().runBackboneRedisCommand(redis -> Boolean.valueOf(redis.hget("basic:messageSettings:" + player.toString(), "soundsDisabled")));
    }

    public Boolean toggleSounds(UUID player) {
        Boolean newValue = !this.isSoundsDisabled(player);
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.hset("basic:messageSettings:" + player.toString(), "soundsDisabled", newValue.toString()));
        return newValue;
    }

    public Boolean isIgnored(UUID player, UUID target) {
        return qLib.getInstance().runBackboneRedisCommand(redis -> Boolean.parseBoolean(redis.hget("basic:messageSettings:ignoreList:" + target.toString(), player.toString())));
    }

    public void addToIgnoreList(UUID player, UUID target) {
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.hset("basic:messageSettings:ignoreList:" + player.toString(), target.toString(), "true"));
    }

    public void removeFromIgnoreList(UUID player, UUID target) {
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.hdel("basic:messageSettings:ignoreList:" + player.toString(), target.toString()));
    }

    public void clearIgnoreList(UUID player) {
        qLib.getInstance().runBackboneRedisCommand(redis -> redis.del("basic:messageSettings:ignoreList:" + player.toString()));
    }

    public List<UUID> getIgnoreList(UUID player) {
        return qLib.getInstance().runBackboneRedisCommand(redis -> redis.hgetAll("basic:messageSettings:ignoreList:" + player.toString()).keySet().stream().map(UUID::fromString).collect(Collectors.toList()));
    }

    public boolean canMessage(Player sender, Player target) {
        if (sender.hasPermission("basic.staff")) {
            return true;
        }
        if (this.isIgnored(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has messaging disabled.");
            return false;
        }
        if (this.isMessagesDisabled(target.getUniqueId())) {
            sender.sendMessage(target.getDisplayName() + ChatColor.RED + " has messages turned off.");
            return false;
        }
        return true;
    }

    public void setIgnoresInvis(Player player, boolean newValue) {
        if (newValue) {
            this.ignoresInvis.add(player.getUniqueId());
        } else {
            this.ignoresInvis.remove(player.getUniqueId());
        }
    }

    public boolean ignoresInvis(Player player) {
        return this.ignoresInvis.contains(player.getUniqueId());
    }

    public Set<UUID> getGlobalSpyEnabled() {
        return this.globalSpyEnabled;
    }

    public Map<UUID, Set<UUID>> getPlayerSpy() {
        return this.playerSpy;
    }

    public Set<UUID> getIgnoresInvis() {
        return this.ignoresInvis;
    }
}

