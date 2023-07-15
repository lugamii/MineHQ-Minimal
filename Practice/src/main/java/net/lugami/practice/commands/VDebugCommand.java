package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.Practice;
import net.minecraft.server.v1_7_R4.EntityTracker;
import net.minecraft.server.v1_7_R4.EntityTrackerEntry;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class VDebugCommand {

    @Command(names = {"vdebug"}, permission = "op")
    public static void vdebug(Player sender, @Param(name="a") Player a, @Param(name="b") Player b, @Param(name="modify", defaultValue = "0") int modify) {
        CraftPlayer aCraft = (CraftPlayer) a;
        CraftPlayer bCraft = (CraftPlayer) b;
        EntityTracker tracker = ((WorldServer) aCraft.getHandle().world).tracker;
        EntityTrackerEntry aTracker = (EntityTrackerEntry) tracker.trackedEntities.get(aCraft.getHandle().getId());
        EntityTrackerEntry bTracker = (EntityTrackerEntry) tracker.trackedEntities.get(bCraft.getHandle().getId());

        if (modify == 1) {
            a.showPlayer(b);
            b.showPlayer(a);

            sender.sendMessage(ChatColor.RED + "Performed soft modify.");
            return;
        } else if (modify == 2) {
            aCraft.getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(bCraft.getHandle()));
            bCraft.getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(aCraft.getHandle()));

            sender.sendMessage(ChatColor.RED + "Performed hard modify.");
            return;
        } else if (modify == 3) {
            a.hidePlayer(b);
            b.hidePlayer(a);

            Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
                a.showPlayer(b);
                b.showPlayer(a);
            }, 10L);

            sender.sendMessage(ChatColor.RED + "Performed flicker modify.");
            return;
        }

        sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + a.getName() + " <-> " + b.getName() + ":");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.BLUE + "a Validation: " + ChatColor.WHITE + (aCraft.getHandle().playerConnection != null && !a.equals(b)));
        sender.sendMessage(ChatColor.BLUE + "b Validation: " + ChatColor.WHITE + (bCraft.getHandle().playerConnection != null && !b.equals(a)));

        sender.sendMessage(ChatColor.BLUE + "a.canSee(b): " + ChatColor.WHITE + a.canSee(b));
        sender.sendMessage(ChatColor.BLUE + "b.canSee(a): " + ChatColor.WHITE + b.canSee(a));

        sender.sendMessage(ChatColor.BLUE + "a Tracker Entry: " + ChatColor.WHITE + aTracker);
        sender.sendMessage(ChatColor.BLUE + "b Tracker Entry: " + ChatColor.WHITE + bTracker);

        sender.sendMessage(ChatColor.BLUE + "aTracker.trackedPlayers.contains(b): " + ChatColor.WHITE + aTracker.trackedPlayers.contains(bCraft.getHandle()));
        sender.sendMessage(ChatColor.BLUE + "bTracker.trackedPlayers.contains(a): " + ChatColor.WHITE + bTracker.trackedPlayers.contains(aCraft.getHandle()));
    }

}