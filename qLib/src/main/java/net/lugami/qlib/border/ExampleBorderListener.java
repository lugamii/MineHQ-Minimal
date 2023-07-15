package net.lugami.qlib.border;

import net.lugami.qlib.border.event.BorderChangeEvent;
import net.lugami.qlib.border.event.PlayerEnterBorderEvent;
import net.lugami.qlib.border.event.PlayerExitBorderEvent;
import net.lugami.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExampleBorderListener implements Listener {

    @EventHandler
    public void onBorderChange(BorderChangeEvent event) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lBorder size has " + (event.getAction() == BorderTask.BorderAction.SHRINK ? "&cdecreased" : "&aincreased&c&l!")));
        if (event.getBorder().getPhysicalBounds().getSizeX() <= 500 && event.getBorder().getPhysicalBounds().getSizeZ() <= 500) {
            event.getBorder().getBorderTask().setAction(BorderTask.BorderAction.NONE);
        }
    }

    @EventHandler
    public void onBorderExit(PlayerExitBorderEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "Warning! You have left the border!");
        Location playerLocation = event.getPlayer().getLocation();
        Border border = event.getBorder();
        Cuboid cuboid = border.getPhysicalBounds();
        int minX = cuboid.getLowerX();
        int minZ = cuboid.getLowerZ();
        int maxX = cuboid.getUpperX();
        int maxZ = cuboid.getUpperZ();
        int newX = playerLocation.getBlockX();
        int newZ = playerLocation.getBlockZ();
        if (maxX < playerLocation.getBlockX()) {
            newX = maxX;
        } else if (minX > playerLocation.getBlockX()) {
            newX = minX;
        }
        if (maxZ < playerLocation.getBlockZ()) {
            newZ = maxZ;
        } else if (minZ > playerLocation.getBlockZ()) {
            newZ = minZ;
        }
        event.getPlayer().teleport(new Location(playerLocation.getWorld(), newX, playerLocation.getY(), newZ));
    }

    @EventHandler
    public void onBorderEnter(PlayerEnterBorderEvent event) {
        event.getPlayer().sendMessage(ChatColor.GREEN + "You have entered the border");
    }
}

