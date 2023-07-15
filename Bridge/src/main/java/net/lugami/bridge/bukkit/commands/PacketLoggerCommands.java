package net.lugami.bridge.bukkit.commands;

import com.google.common.collect.Maps;
import net.lugami.bridge.global.packetlogger.PacketLog;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.BukkitAPI;

import java.util.Map;

public class PacketLoggerCommands {

    @Command(names = {"packetlogger info"}, permission = "op", hidden = true, async = true)
    public static void packetloggerinfo(CommandSender sender, @Param(name = "target", defaultValue = "self") Player target) {

        sender.sendMessage(ChatColor.GREEN + "Getting Packet Log for " + target.getDisplayName() + ChatColor.GREEN + "...");
        sender.sendMessage(BukkitAPI.LINE);

        Map<String, Integer> packetInfo = Maps.newHashMap();
        Map<String, Integer> canceled = Maps.newHashMap();
        for (PacketLog packetLog : BridgeGlobal.getPacketLogHandler().getLog(target.getUniqueId())) {

            int amount = 1;
            if (packetInfo.containsKey(packetLog.getPacket().getClass().getSimpleName())) {
                amount = packetInfo.get(packetLog.getPacket().getClass().getSimpleName());
                amount++;
            }
            int amountFlagged = 1;
            if (canceled.containsKey(packetLog.getPacket().getClass().getSimpleName())) {
                amountFlagged = canceled.get(packetLog.getPacket().getClass().getSimpleName());
                amountFlagged++;
            }
            canceled.put(packetLog.getPacket().getClass().getSimpleName(), amount);
        }
        if (packetInfo.size() == 0) {
            sender.sendMessage(ChatColor.RED + target.getName() + " has no packet logs.");
        } else {
            for (Map.Entry<String, Integer> entry : packetInfo.entrySet()) {
                sender.sendMessage(BukkitAPI.LINE);
                sender.sendMessage(ChatColor.YELLOW + entry.getKey() + ChatColor.GRAY + " (" + ChatColor.RED + entry.getValue() + ChatColor.GRAY + ") " +
                        ChatColor.YELLOW + "Times Flagged: " + canceled.get(entry.getKey()));
            }
            sender.sendMessage(BukkitAPI.LINE);


        }
    }

    @Command(names = {"packetlogger toggle "}, permission = "op", hidden = true, async = true)
    public static void packetloggertoggle(CommandSender sender) {
        boolean toogled = Bridge.getInstance().togglePacketLogger;
        Bridge.getInstance().setTogglePacketLogger(!Bridge.getInstance().togglePacketLogger);
        sender.sendMessage(ChatColor.YELLOW + "PacketLogger: " + (!toogled ? ChatColor.RED + "Disabled" : ChatColor.GREEN + "Enabled") + ChatColor.YELLOW + ".");


    }
}
