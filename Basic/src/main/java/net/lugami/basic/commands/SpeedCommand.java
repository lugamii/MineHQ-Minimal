package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpeedCommand {

    @Command(names={"speed"}, permission="basic.speed", description = "Change your walk or fly speed")
    public static void speed(Player sender, @Param(name="speed") int speed) {
        if (speed < 0 || speed > 10) {
            sender.sendMessage(ChatColor.RED + "Speed must be between 0 and 10.");
            return;
        }
        boolean fly = sender.isFlying();
        if (fly) {
            sender.setFlySpeed(SpeedCommand.getSpeed(speed, true));
        } else {
            sender.setWalkSpeed(SpeedCommand.getSpeed(speed, false));
        }
        sender.sendMessage(ChatColor.GOLD + (fly ? "Fly" : "Walk") + " set to " + ChatColor.WHITE + speed + ChatColor.GOLD + ".");
    }

    private static float getSpeed(int speed, boolean isFly) {
        float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1.0f;
        if ((float)speed < 1.0f) {
            return defaultSpeed * (float)speed;
        }
        float ratio = ((float)speed - 1.0f) / 9.0f * (1.0f - defaultSpeed);
        return ratio + defaultSpeed;
    }
}

