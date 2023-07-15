package net.lugami.basic.commands;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R4.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;

public class SetSlotsCommand {

    private static Field maxPlayerField = null;

    @Command(names={"setslots", "setmaxslots", "setservercap", "ssc"}, permission="basic.setslots", description = "Set the max slots")
    public static void setslots(CommandSender sender, @Param(name="slots") int slots) {
        if (slots < 0) {
            sender.sendMessage(ChatColor.RED + "The number of slots must be greater or equal to zero.");
            return;
        }
        SetSlotsCommand.set(slots);
        sender.sendMessage(ChatColor.GOLD + "Slots set to " + ChatColor.WHITE + slots + ChatColor.GOLD + ".");
    }

    private static void set(int slots) {
        try {
            maxPlayerField.set(((CraftServer)Bukkit.getServer()).getHandle(), slots);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        SetSlotsCommand.save();
    }

    private static void save() {
        Basic.getInstance().getConfig().set("slots", Bukkit.getMaxPlayers());
        Basic.getInstance().saveConfig();
    }

    public static void load() {
        if (Basic.getInstance().getConfig().contains("slots")) {
            SetSlotsCommand.set(Basic.getInstance().getConfig().getInt("slots"));
        } else {
            Basic.getInstance().getConfig().set("slots", Bukkit.getMaxPlayers());
        }
    }

    static {
        try {
            maxPlayerField = PlayerList.class.getDeclaredField("maxPlayers");
            maxPlayerField.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

