package net.lugami.bridge.bukkit.commands;

import net.lugami.bridge.bukkit.parameters.AlertPacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.xpacket.FrozenXPacketHandler;
import org.bukkit.command.CommandSender;

public class AlertCommand {

    @Command(names = "alert", permission = "bridge.alert", description = "Alert a message to the network", async = true)
    public static void alert(CommandSender sender, @Param(name = "message", wildcard = true) String msg) {
            FrozenXPacketHandler.sendToAll(new AlertPacket(msg, false));
        }

    @Command(names = "rawalert", permission = "bridge.alert", description = "Alert a raw message to the network", async = true)
    public static void rawalert(CommandSender sender, @Param(name = "message", wildcard = true) String msg) {
            FrozenXPacketHandler.sendToAll(new AlertPacket(msg, true));
        }
    }