package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.EntityUtils;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MobCommand {

    @Command(names={"spawnmob", "mob"}, permission="basic.spawnmob", description = "Spawn mobs! Supports stacking")
    public static void spawnmob(Player sender, @Param(name="mob[,mob,mob...]") String mobs, @Param(name="amount", defaultValue="1") int amount) {
        String[] split = mobs.split(",");
        ArrayList<EntityType> types = new ArrayList<EntityType>();
        for (String part : split) {
            EntityType type = EntityUtils.parse(part);
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "Mob '" + part + "' not found.");
                return;
            }
            if (!type.isAlive()) {
                sender.sendMessage(ChatColor.RED + "Entity type '" + part + "' is not a valid mob.");
                return;
            }
            types.add(type);
        }
        if (sender.getTargetBlock(null, 30) == null) {
            sender.sendMessage(ChatColor.RED + "Please look at a block.");
            return;
        }
        if (types.size() == 0) {
            sender.sendMessage(ChatColor.RED + "Idk how you got here but um... Nope.");
            return;
        }
        Location location = sender.getTargetBlock(null, 30).getLocation().add(0.0, 1.0, 0.0);
        int totalAmount = 0;
        for (int i = 0; i < amount; ++i) {
            Entity current = sender.getWorld().spawnEntity(location, (EntityType)types.get(0));
            ++totalAmount;
            for (int x = 1; x < types.size(); ++x) {
                Entity newEntity = sender.getWorld().spawnEntity(location, (EntityType)types.get(x));
                current.setPassenger(newEntity);
                ++totalAmount;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "Spawned " + ChatColor.WHITE + totalAmount + ChatColor.GOLD + " entities.");
    }
}

