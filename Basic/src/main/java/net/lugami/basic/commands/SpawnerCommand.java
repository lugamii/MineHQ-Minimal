package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnerCommand {

    @Command(names={"spawner"}, permission="basic.spawner", description = "Change a spawner's type")
    public static void spawner(Player sender, @Param(name="mob") String mob) {
        EntityType type = EntityUtils.parse(mob);
        if (type == null || !type.isAlive()) {
            sender.sendMessage(ChatColor.RED + "No mob with the name " + mob + " found.");
            return;
        }
        Block block = sender.getTargetBlock(null, 5);
        if (block == null || !(block.getState() instanceof CreatureSpawner)) {
            sender.sendMessage(ChatColor.RED + "You aren't looking at a mob spawner.");
            return;
        }
        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        spawner.setSpawnedType(type);
        spawner.update();
        sender.sendMessage(ChatColor.GOLD + "This spawner now spawns " + ChatColor.WHITE + EntityUtils.getName((EntityType)type) + ChatColor.GOLD + ".");
    }
}

