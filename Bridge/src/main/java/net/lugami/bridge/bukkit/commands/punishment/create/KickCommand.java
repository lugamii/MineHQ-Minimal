package net.lugami.bridge.bukkit.commands.punishment.create;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Flag;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.PunishmentPacket;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

public class KickCommand
{
    @Command(names = { "kick", "k" }, permission = "basic.staff", description = "Kick a player from the server")
    public static void pMuteCmd(CommandSender s, @Flag(value = {"a", "announce"}, description = "Announce this kick to the server") boolean silent, @Param(name = "target") Profile target, @Param(name = "reason", defaultValue = "Kicked by a staff member", wildcard = true) String reason) {
        Profile pf = BukkitAPI.getProfile(s);

        if (!BukkitAPI.canOverride(pf, target)) {
            s.sendMessage(ChatColor.RED + "You cannot punish this player.");
            return;
        }

        Punishment punishment = new Punishment(target, pf, BridgeGlobal.getSystemName(), reason, PunishmentType.KICK, new HashSet<>(),false, !silent, false, Long.MAX_VALUE);
        target.getPunishments().add(punishment);
        pf.getStaffPunishments().add(punishment);
        target.saveProfile();
        pf.saveProfile();
        PacketHandler.sendToAll(new PunishmentPacket(punishment));
    }
}