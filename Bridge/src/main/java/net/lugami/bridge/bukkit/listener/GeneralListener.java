package net.lugami.bridge.bukkit.listener;

import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.qlib.util.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.util.BaseEvent;
import mkremins.fanciful.FancyMessage;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GeneralListener {


    public static void updatePermissions(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {

            Profile profile = BukkitAPI.getProfile(uuid);
            profile.updateColor();
            PermissionAttachment permissionAttachment = player.addAttachment(Bridge.getInstance());
            List<String> perms = profile.getPermissions().keySet().stream().filter(profile::hasPermission).collect(Collectors.toList());
            profile.getPermissions().keySet().stream().filter(profile::hasPermission).forEach(permission->{

                if (permission.startsWith("-")) {
                    permission = permission.substring(1);
                    permissionAttachment.unsetPermission(permission);
                } else {
                    permissionAttachment.setPermission(permission, true);
                }

            });

            profile.getActiveGrants().forEach(grant->{
                perms.addAll(grant.getRank().getPermissions().keySet().stream().filter(s -> grant.getRank().hasPermission(s)).collect(Collectors.toList()));
                grant.getRank().getPermissions().keySet().stream().filter(s -> grant.getRank().hasPermission(s)).forEach(permission->{
                    if (permission.startsWith("-")) {
                        permission = permission.substring(1);
                        permissionAttachment.unsetPermission(permission);
                    } else {
                        permissionAttachment.setPermission(permission, true);
                    }
                });
                grant.getRank().getInherits().forEach(inherit->{
                    perms.addAll(inherit.getPermissions().keySet().stream().filter(inherit::hasPermission).collect(Collectors.toList()));
                    inherit.getPermissions().keySet().stream().filter(inherit::hasPermission).forEach(permission -> {
                        if (permission.startsWith("-")) {
                            permission = permission.substring(1);
                            permissionAttachment.unsetPermission(permission);
                        } else {
                            permissionAttachment.setPermission(permission, true);
                        }
                    });
                });
            });
            player.recalculatePermissions();
            profile.setActivePermissions(perms);

            String totpMessage = null;

            if (profile.isTotpRequired() && !player.hasMetadata("ForceAuth")) {
                if (profile.getSecretKey().isEmpty() && profile.getCurrentGrant().getRank().isStaff()) {
                    totpMessage = ChatColor.RED + ChatColor.BOLD.toString() + "Please setup your 2FA code";
                } else {
                    totpMessage = ChatColor.RED + ChatColor.BOLD.toString() + "Please enter your 2FA code";
                }

                player.setMetadata("Locked", new FixedMetadataValue(Bridge.getInstance(), totpMessage));
            } else {
                player.removeMetadata("Locked", Bridge.getInstance());
            }

            String finalTotpMessage = totpMessage;
            (new BukkitRunnable() {
                public void run() {
                    if (finalTotpMessage != null) {
                        player.sendMessage(finalTotpMessage);
                    }

                }
            }).runTaskLater(Bridge.getInstance(), 10L);

        }
    }

    public static void updateColor(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setDisplayName(BukkitAPI.getPlayerRank(player).getColor() + player.getName() + "§r");
            if(player.hasMetadata("RankPrefix")) player.removeMetadata("RankPrefix", Bridge.getInstance());
            player.setMetadata("RankPrefix", new FixedMetadataValue(Bridge.getInstance(), (BukkitAPI.getPlayerRank(player).getPrefix())));
        }
    }

    public static String getServerStatus() {
        return (Bridge.getInstance().isBooted() ? (Bukkit.hasWhitelist() ? "WHITELISTED" : "ONLINE") : "BOOTING");
    }

    public static void refreshPlayersInRank(Rank rank) {
        BridgeGlobal.getProfileHandler().getProfiles().stream().filter(profile-> Bukkit.getPlayer(profile.getUuid()) != null && profile.hasActiveGrantOf(rank)).forEach(Profile::refreshCurrentGrant);
    }

    public static void logMessages(String msg, boolean packetIncoming) {
        if(packetIncoming) Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> player.sendMessage(msg));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        if(BridgeGlobal.getProfileHandler() != null && !BridgeGlobal.getProfileHandler().getProfiles().isEmpty()) BridgeGlobal.getProfileHandler().getProfiles().stream().filter(profile -> Bukkit.getPlayer(profile.getUuid()) != null && profile.isDebug()).forEach(profile -> Bukkit.getPlayer(profile.getUuid()).sendMessage(msg));
    }

    public static void broadcastMessage(String msg, String permission) {
        String m = ChatColor.translateAlternateColorCodes('&', msg);
        Bukkit.getConsoleSender().sendMessage(m);
        if(permission.equals("")) Bukkit.broadcastMessage(m);
        else Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).forEach(p -> p.sendMessage(m));
    }

    public static void broadcastMessage(String msg) {
        broadcastMessage(msg, "");
    }

    public static void broadcastMessage(BaseComponent[] msg, String permission) {
        Bukkit.getConsoleSender().sendMessage(TextComponent.toPlainText(msg));
        if(permission.equals("")) {
            Bukkit.getOnlinePlayers().forEach(p -> p.spigot().sendMessage(msg));
        }else {
            Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).forEach(p -> p.spigot().sendMessage(msg));
        }
    }

    public static void sendMessage(String msg, List<Player> players) {
        players.forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg)));
    }

    public static void sendMessage(BaseComponent[] msg, List<Player> players) {
        players.forEach(p -> p.spigot().sendMessage(msg));
    }

    public static void broadcastMessage(BaseComponent[] msg) {
        broadcastMessage(msg, "");
    }

    public static void callEvent(Object cls) {
        ((BaseEvent)cls).call();
    }

    public static void shutdown() {
        Bukkit.shutdown();
    }

    public static boolean hasPermission(Profile profile, String permisson) {
        return Bukkit.getOfflinePlayer(profile.getUuid()).isOp() || profile.getActiveGrants().parallelStream().filter(grant -> grant.isGrantActiveOnScope() && grant.isStillActive()).anyMatch(grant -> grant.getRank().hasPermission(permisson) || profile.hasPermission(permisson) || profile.getActivePermissions().contains(permisson));
    }

    public static boolean isOP(Profile profile) {
        if(profile == null || Bukkit.getOfflinePlayer(profile.getUuid()) == null) return false;
        return Bukkit.getOfflinePlayer(profile.getUuid()).isOp();
    }

    private boolean isRunningOnBungee(){
        return SpigotConfig.bungee && (!(Bukkit.getServer().getOnlineMode()));
    }

    public static void handlePunishment(Punishment punishment, boolean pardon) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            FancyMessage m = new FancyMessage((punishment.isSilent() ? ChatColor.GRAY + "[Silent] " + ChatColor.GREEN : "") +
                    BukkitAPI.getColor(punishment.getTarget()) + (punishment.getTarget().getDisguise() != null ? punishment.getTarget().getDisguise().getDisguiseName() : punishment.getTarget().getUsername()) +
                            ChatColor.GREEN + " was " + punishment.getDisplayName() +
                            " by " +
                    (pardon ? BukkitAPI.getColor(punishment.getPardonedBy()) + (punishment.getPardonedBy().getDisguise() != null ? punishment.getPardonedBy().getDisguise().getDisguiseName() : punishment.getPardonedBy().getUsername()) + ChatColor.GREEN + "." : BukkitAPI.getColor(punishment.getExecutor()) + (punishment.getExecutor().getDisguise() != null ? punishment.getExecutor().getDisguise().getDisguiseName() : punishment.getExecutor().getUsername()) +
                            ChatColor.GREEN + "."));
            m.command("/c " + punishment.getTarget().getUsername());

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + (pardon ? punishment.getPardonedReason() : punishment.getReason()));
            if(!pardon && !punishment.getPunishmentType().equals(PunishmentType.KICK)) lore.add(ChatColor.YELLOW + "Duration: " + ChatColor.RED + punishment.getRemainingString());
            lore.add(ChatColor.YELLOW + "Click to view on website");


            boolean staff = BukkitAPI.getPlayerRank(p, true).isStaff();
            if(staff) m.tooltip(lore);
            if(punishment.isSilent()) {
                if(staff) m.send(p);
            }else {
                m.send(p);
            }
        });

        if(pardon) {
            Punishment p = punishment.getTarget().getPunishmentByID(punishment.getUuid());
            p.setPardoned(pardon);
            p.setPardonedAt(punishment.getPardonedAt());
            p.setPardonedBy(punishment.getPardonedBy().getUuid());
            p.setPardonedReason(punishment.getPardonedReason());
            p.setPardonedServer(punishment.getPardonedServer());
        }else {

            if(punishment.isIP()) {
                Bukkit.getOnlinePlayers().stream().filter(p -> BukkitAPI.getProfile(p) != null && BukkitAPI.getProfile(p).getCurrentIPAddress().equals(punishment.getTarget().getCurrentIPAddress())).forEach(target -> {
                    if(target != null) Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
                        BukkitAPI.getProfile(target).getPunishments().add(punishment);
                        if(punishment.isClear()) PlayerUtils.resetInventory(target);
                        if(punishment.getPunishmentType() != PunishmentType.WARN && punishment.getPunishmentType() != PunishmentType.MUTE) {
                            target.kickPlayer(getPunishmentMessage(punishment));
                        }else if(punishment.getPunishmentType() == PunishmentType.WARN){
                            target.sendMessage("");
                            target.sendMessage("");
                            target.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "You have been warned: " + ChatColor.YELLOW.toString() + ChatColor.BOLD + punishment.getReason());
                            target.sendMessage("");
                            target.sendMessage("");
                        }else if(punishment.getPunishmentType() == PunishmentType.MUTE) {
                            target.sendMessage(ChatColor.RED + "You have been muted.");
                            target.sendMessage(ChatColor.RED + (punishment.isPermanent() ? "This mute is permanent." : "Time remaining: " + TimeUtils.formatIntoDetailedString((int) (punishment.getDuration() / 1000))));
                        }
                    });
                    if(punishment.isClear()) PlayerUtils.resetInventory(target);
                    if(punishment.getPunishmentType() != PunishmentType.WARN && punishment.getPunishmentType() != PunishmentType.MUTE) target.kickPlayer(getPunishmentMessage(punishment));
                });
            }else {
                Player target = Bukkit.getPlayer(punishment.getTarget().getUuid());
                if(target != null) Bukkit.getScheduler().runTask(Bridge.getInstance(), () -> {
                    if(punishment.isClear()) PlayerUtils.resetInventory(target);
                    if(punishment.getPunishmentType() != PunishmentType.WARN && punishment.getPunishmentType() != PunishmentType.MUTE) {
                        target.kickPlayer(getPunishmentMessage(punishment));
                    }else if(punishment.getPunishmentType() == PunishmentType.WARN){
                        target.sendMessage("");
                        target.sendMessage("");
                        target.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You have been warned: " + ChatColor.YELLOW + ChatColor.BOLD + punishment.getReason());
                        target.sendMessage("");
                        target.sendMessage("");
                    }else if(punishment.getPunishmentType() == PunishmentType.MUTE) {
                        target.sendMessage(ChatColor.RED + "You have been muted.");
                        target.sendMessage(ChatColor.RED + (punishment.isPermanent() ? "This mute is permanent." : "Time remaining: " + TimeUtils.formatIntoDetailedString((int) (punishment.getDuration() / 1000))));
                    }
                });
            }
        }
    }

    public static String getPunishmentMessage(Punishment punishment, String IP) {
        String msg = "";
        switch(punishment.getPunishmentType()) {
            case BLACKLIST: {
                msg = ChatColor.RED + "Your account has been blacklisted from the " + BridgeGlobal.getServerDisplayName() + "\n\nThis type of punishment cannot be appealed.";
                break;
            }
            case BAN: {
                msg = ChatColor.RED + "Your account has been suspended from the " + BridgeGlobal.getServerDisplayName() + (!punishment.isPermanent() ? "\n\nExpires in " + punishment.getRemainingString() + "." : ChatColor.RED + "\n\nAppeal at " + BridgeGlobal.getServerWebsite() + "/appeal");
                break;
            }
            case KICK: {
                msg = ChatColor.RED + "You were kicked: " + punishment.getReason();
            }
        }
        return msg + (punishment.getPunishmentType() != PunishmentType.BLACKLIST && punishment.isIP() && !IP.equals("") && punishment.getTarget().getCurrentIPAddress().equals(IP) ? "\n\n" + ChatColor.RED + "This punishment is assiciated with " + punishment.getTarget().getUsername() : "");
    }

    public static String getPunishmentMessage(Punishment punishment) {
        return getPunishmentMessage(punishment, "");
    }






}
