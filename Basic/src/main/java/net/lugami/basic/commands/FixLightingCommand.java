package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.cuboid.Cuboid;
import net.minecraft.server.v1_7_R4.EnumSkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;

public class FixLightingCommand {

    @Command(names={"fixlighting"}, permission="op")
    public static void fixlighting(Player sender, @Param(name="radius", defaultValue="30") int radius) {
        if (Bukkit.getOnlinePlayers().size() > 20) {
            sender.sendMessage(ChatColor.RED + "You cannot do this with more than 20 players online.");
            return;
        }
        if (radius > 500) {
            sender.sendMessage(ChatColor.RED + "Radius cannot exceed 500.");
            return;
        }
        Cuboid cuboid = new Cuboid(sender.getLocation(), sender.getLocation()).outset(Cuboid.CuboidDirection.HORIZONTAL, radius).outset(Cuboid.CuboidDirection.VERTICAL, radius);
        for (Block block : cuboid) {
            ((CraftWorld)sender.getLocation().getWorld()).getHandle().updateLight(EnumSkyBlock.BLOCK, block.getX(), block.getY(), block.getZ());
        }
        int volume = cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ();
        sender.sendMessage(ChatColor.YELLOW + "Recalculated the lighting of " + volume + " blocks.");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + sender.getName() + " recalculated the lighting of " + volume + " blocks.");
    }
}

