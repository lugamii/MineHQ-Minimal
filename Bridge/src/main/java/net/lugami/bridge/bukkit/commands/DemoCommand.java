package net.lugami.bridge.bukkit.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class DemoCommand {

    @Command(names = "demo", permission = "op", hidden = true)
    public static void demo(Player player, @Param(name = "target") Player target) {
        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(5, 0);
        ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
        player.sendMessage(ChatColor.YELLOW + "Demo screened " + target.getDisplayName() + ChatColor.YELLOW + "!");
    }

}
