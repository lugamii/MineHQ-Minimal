package net.lugami.basic.commands;

import com.google.common.base.Joiner;
import net.lugami.basic.Basic;
import net.lugami.basic.chat.MessagingManager;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.parameters.packets.filter.FilterViolationPacket;
import net.lugami.bridge.global.filter.Filter;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.command.Type;
import net.lugami.qlib.command.parameter.filter.NormalFilter;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.qlib.xpacket.FrozenXPacketHandler;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MessagingCommands {

    @Command(names={"message", "msg", "m", "whisper", "w", "tell", "t"}, permission= "", description= "Send a player a private message", async=true)
    public static void message(Player sender, @Param(name="player") Player target, @Param(name="message", wildcard=true) @Type(value=NormalFilter.class) String message) {
        if (!Basic.getInstance().getMessagingManager().canMessage(sender, target)) {
            return;
        }

        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(sender.getUniqueId());
        if (profile.isMuted()) {
            sender.sendMessage(ChatColor.RED + "You can't message players as you are muted.");
            return;
        }

        MessagingManager manager = Basic.getInstance().getMessagingManager();
        if (manager.isMessagesDisabled(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have messages toggled off.");
            return;
        }
        MessagingCommands.sendMessage(sender, target, message);
    }

    @Command(names={"reply", "r"}, permission= "", description = "Reply to the player you're in a conversation with", async=true)
    public static void reply(Player sender, @Param(name="message", defaultValue="   ", wildcard=true) @Type(value=NormalFilter.class) String message) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        UUID lastMessaged = manager.getLastMessaged(sender.getUniqueId());
        if (message.equals("   ")) {
            if (lastMessaged == null) {
                sender.sendMessage(ChatColor.RED + "You aren't in a conversation.");
            } else {
                sender.sendMessage(ChatColor.GOLD + "You are in a conversation with " + ChatColor.WHITE + UUIDUtils.name(lastMessaged) + ChatColor.GOLD + ".");
            }
            return;
        }
        if (lastMessaged == null) {
            sender.sendMessage(ChatColor.RED + "You have no one to reply to.");
            return;
        }
        if (manager.isMessagesDisabled(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have messages toggled off.");
            return;
        }
        Player target = Bukkit.getPlayer(lastMessaged);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player has logged out.");
            return;
        }
        if (!Basic.getInstance().getMessagingManager().canMessage(sender, target)) {
            return;
        }
        MessagingCommands.sendMessage(sender, target, message);
    }

    @Command(names={"sounds"}, permission = "", description = "Toggle messaging sounds", async=true)
    public static void sounds(Player sender) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        boolean toggle = manager.toggleSounds(sender.getUniqueId());
        if (toggle) {
            sender.sendMessage(ChatColor.YELLOW + "Messaging sounds have been disabled.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Messaging sounds have been enabled.");
        }
    }

    @Command(names={"invismsg"}, permission="basic.staff", description = "Toggle invis messaging")
    public static void invismsg(Player sender) {
        MessagingManager manage = Basic.getInstance().getMessagingManager();
        if (manage.ignoresInvis(sender)) {
            manage.setIgnoresInvis(sender, false);
            sender.sendMessage(ChatColor.YELLOW + "Only staff members will be able to message you while you're invisible from now on.");
        } else {
            manage.setIgnoresInvis(sender, true);
            sender.sendMessage(ChatColor.YELLOW + "Everyone is now able to message you, even if you're invisible.");
        }
    }

    @Command(names={"ignore"}, permission = "", description = "Start ignoring a player. You won't receive private messages from them or see their public chat messages", async=true)
    public static void ignore(Player sender, @Param(name="player") UUID target) {
        if (sender.getUniqueId().equals(target)) {
            sender.sendMessage(ChatColor.RED + "You can't ignore yourself.");
            return;
        }
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        if (manager.isIgnored(target, sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already ignoring " + ChatColor.WHITE + UUIDUtils.name(target) + ChatColor.RED + ".");
            return;
        }
        manager.addToIgnoreList(sender.getUniqueId(), target);
        sender.sendMessage(ChatColor.YELLOW + "Now ignoring " + ChatColor.WHITE + UUIDUtils.name(target) + ChatColor.YELLOW + ".");
    }

    @Command(names={"ignore remove", "unignore"}, permission = "", description = "Stop ignoring a player", async=true)
    public static void ignore_remove(Player sender, @Param(name="player") UUID target) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        if (!manager.isIgnored(target, sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You aren't ignoring " + UUIDUtils.name(target) + ".");
            return;
        }
        manager.removeFromIgnoreList(sender.getUniqueId(), target);
        sender.sendMessage(ChatColor.GREEN + "You are no longer ignoring " + UUIDUtils.name(target) + ".");
    }

    @Command(names={"ignore list"}, permission = "", description = "See a list of people you're currently ignoring", async=true)
    public static void ignore_list(Player sender) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        List<UUID> ignoreList = manager.getIgnoreList(sender.getUniqueId());
        if (ignoreList.size() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "You aren't ignoring anyone.");
            return;
        }
        TreeSet<String> names = ignoreList.stream().map(UUIDUtils::name).collect(Collectors.toCollection(TreeSet::new));
        StringBuilder message = new StringBuilder();
        for (String name : names) {
            message.append(ChatColor.RED).append(name).append(ChatColor.YELLOW).append(", ");
        }
        if (message.length() > 2) {
            message.setLength(message.length() - 2);
        }
        sender.sendMessage(ChatColor.YELLOW + "You are currently ignoring " + ChatColor.RED + names.size() + ChatColor.YELLOW + " player" + (names.size() == 1 ? "" : "s") + ": " + message.toString());
    }

    @Command(names={"spy all"}, permission="basic.spy", description = "Toggle global private message spying")
    public static void spy_all(Player sender) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        if (manager.getGlobalSpyEnabled().contains(sender.getUniqueId())) {
            manager.getGlobalSpyEnabled().remove(sender.getUniqueId());
        } else {
            manager.getGlobalSpyEnabled().add(sender.getUniqueId());
        }
        sender.sendMessage(ChatColor.GOLD + "Global chat spy has been set to " + ChatColor.WHITE + manager.getGlobalSpyEnabled().contains(sender.getUniqueId()) + ChatColor.GOLD + ".");
    }

    @Command(names={"spy player"}, permission="basic.spy", description = "Toggle player-targeted message spying")
    public static void spy_player(Player sender, @Param(name="player") UUID player) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        manager.getPlayerSpy().putIfAbsent(sender.getUniqueId(), new HashSet());
        if (manager.getPlayerSpy().get(sender.getUniqueId()).contains(player)) {
            manager.getPlayerSpy().get(sender.getUniqueId()).remove(player);
            sender.sendMessage(ChatColor.GOLD + "You are no longer spying on " + ChatColor.WHITE + UUIDUtils.name(player) + ChatColor.WHITE + "'s private messages.");
        } else {
            manager.getPlayerSpy().get(sender.getUniqueId()).add(player);
            sender.sendMessage(ChatColor.GOLD + "You are now spying on " + ChatColor.WHITE + UUIDUtils.name(player) + ChatColor.WHITE + "'s private messages.");
        }
    }

    @Command(names={"spy list"}, permission="basic.spy", description = "See a list of players you're currently spying on")
    public static void spy_list(Player sender) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        if (!manager.getPlayerSpy().containsKey(sender.getUniqueId()) || manager.getPlayerSpy().get(sender.getUniqueId()).size() == 0) {
            sender.sendMessage(ChatColor.RED + "You aren't spying on anyone.");
            return;
        }
        List<String> names = manager.getPlayerSpy().get(sender.getUniqueId()).stream().map(UUIDUtils::name).collect(Collectors.toList());
        sender.sendMessage(ChatColor.RED + "You are spying on: " + ChatColor.YELLOW + Joiner.on(", ").join(names));
    }

    @Command(names={"togglepm", "tpm"}, permission = "", description = "Toggle private messaging")
    public static void togglepm(Player sender) {
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        boolean toggle = manager.toggleMessages(sender.getUniqueId());
        if (toggle) {
            sender.sendMessage(ChatColor.RED + "Private messages have been disabled.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Private messages have been enabled.");
        }
    }

    private static void sendMessage(Player sender, Player target, String message) {
        Filter filter = BridgeGlobal.getFilterHandler().isViolatingFilter(net.md_5.bungee.api.ChatColor.stripColor(message));
        if (filter != null && !sender.hasPermission("basic.staff")) {
            FrozenXPacketHandler.sendToAll(new FilterViolationPacket(Bukkit.getServerName(), sender.getName(), target.getName(), message));
            sender.sendMessage(ChatColor.GRAY + "(To " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GRAY + ") " + message);
            return;
        }
        MessagingManager manager = Basic.getInstance().getMessagingManager();
        manager.setLastMessaged(sender.getUniqueId(), target.getUniqueId());
        manager.setLastMessaged(target.getUniqueId(), sender.getUniqueId());
        target.sendMessage(ChatColor.GRAY + "(From " + ChatColor.WHITE + sender.getDisplayName() + ChatColor.GRAY + ") " + message);
        sender.sendMessage(ChatColor.GRAY + "(To " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GRAY + ") " + message);
        if (!manager.isSoundsDisabled(target.getUniqueId())) {
            target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 0.1f);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == sender || player == target) continue;
            if (manager.getPlayerSpy().containsKey(player.getUniqueId()) && (manager.getPlayerSpy().get(player.getUniqueId()).contains(sender.getUniqueId()) || manager.getPlayerSpy().get(player.getUniqueId()).contains(target.getUniqueId()))) {
                player.sendMessage(ChatColor.GRAY + "(" + ChatColor.WHITE + sender.getDisplayName() + ChatColor.GRAY + " to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GRAY + ") " + message);
                continue;
            }
            if (!manager.getGlobalSpyEnabled().contains(player.getUniqueId())) continue;
            player.sendMessage(ChatColor.GRAY + "(" + ChatColor.WHITE + sender.getDisplayName() + ChatColor.GRAY + " to " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GRAY + ") " + message);
        }
    }
}

