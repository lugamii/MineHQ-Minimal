package net.lugami.basic.server;

import net.lugami.basic.idle.IdleCheckRunnable;
import net.lugami.basic.Basic;

import java.util.*;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class ServerManager {

    private boolean frozen;
    private Set<String> disallowedCommands = new HashSet<String>();

    public ServerManager() {
        ArrayList list = (ArrayList) Basic.getInstance().getConfig().getStringList("disallowedCommands");
        if (list == null) {
            list = new ArrayList();
        }
        this.disallowedCommands.addAll(list);
        this.disallowedCommands.add("/calc");
        this.disallowedCommands.add("/calculate");
        this.disallowedCommands.add("/eval");
        this.disallowedCommands.add("/evaluate");
        this.disallowedCommands.add("/solve");
        this.disallowedCommands.add("/worldedit:calc");
        this.disallowedCommands.add("/worldedit:eval");
        if (Basic.getInstance().getConfig().getBoolean("idlekick.enabled")) {
            new IdleCheckRunnable(Basic.getInstance().getConfig().getInt("idlekick.minutes"), Basic.getInstance().getConfig().getString("idlekick.message")).runTaskTimer(Basic.getInstance(), 10L, 10L);
        }
    }

    public void freeze(Player player) {
        player.setMetadata("frozen", new FixedMetadataValue(Basic.getInstance(), true));
        player.sendMessage(ChatColor.RED + "You have been frozen by a staff member.");
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskLater(Basic.getInstance(), () -> this.unfreeze(uuid), 20L * TimeUnit.HOURS.toSeconds(2L));
        Location location = player.getLocation();
        int tries = 0;
        while (1.0 <= location.getY() && !location.getBlock().getType().isSolid() && tries++ < 100) {
            location.subtract(0.0, 1.0, 0.0);
            if (!(location.getY() <= 0.0)) continue;
        }
        if (100 <= tries) {
            Bukkit.getLogger().info("Hit the 100 try limit on the freeze commands.");
        }
        location.setY(location.getBlockY());
        player.teleport(location.add(0.0, 1.0, 0.0));
    }

    public void unfreeze(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.removeMetadata("frozen", Basic.getInstance());
            player.sendMessage(ChatColor.GREEN + "You have been unfrozen by a staff member.");
        }
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public Set<String> getDisallowedCommands() {
        return this.disallowedCommands;
    }
}

