package net.lugami.bridge.bukkit.listener;

import net.lugami.bridge.bukkit.commands.punishment.create.MuteCommand;
import net.lugami.bridge.bukkit.parameters.packets.filter.FilterViolationPacket;
import net.lugami.bridge.global.disguise.DisguisePlayer;
import net.lugami.bridge.global.filter.Filter;
import net.lugami.bridge.global.filter.FilterAction;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.handlers.MongoHandler;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.Tasks;
import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.qlib.chat.ChatPopulator;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import net.lugami.qlib.tab.FrozenTabHandler;
import net.lugami.qlib.xpacket.FrozenXPacketHandler;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.util.EncryptionHandler;
import net.lugami.bridge.global.util.Msg;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BridgeListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginLOWEST(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getPlayer(event.getName());

        if (player != null) {
            BridgeGlobal.getDisguiseManager().undisguise(player, true, true);
            //event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Someone is already online with your name.");
            Tasks.run(() -> player.kickPlayer(ChatColor.RED + "You were kicked because the owner of this disguised name has joined."));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        String loggedInIP = EncryptionHandler.encryptUsingKey(e.getAddress().getHostAddress());
        AtomicReference<Profile> atomicReference = new AtomicReference<>();

        BridgeGlobal.getMongoHandler().loadProfile(uuid.toString(), atomicReference::set, false, MongoHandler.LoadType.UUID);

        Profile profile = atomicReference.get();
        if (profile == null)
            (profile = new Profile(e.getName(), uuid, false)).applyGrant(Grant.getDefaultGrant(), null, false);

        if (loggedInIP != null) {
            profile.checkTotpLock(profile.getUuid(), loggedInIP);

            if (profile.getCurrentIPAddress() == null) profile.setCurrentIPAddress(loggedInIP);

            if (profile.getActivePunishments(PunishmentType.BAN).size() > 0) {
                Punishment punishment = (Punishment) profile.getActivePunishments(PunishmentType.BAN).toArray()[0];
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, GeneralListener.getPunishmentMessage(punishment, punishment.isIP() ? loggedInIP : ""));
                saveLatestIP(profile, loggedInIP);
                return;
            }

            if (profile.getActivePunishments(PunishmentType.BLACKLIST).size() > 0) {
                Punishment punishment = (Punishment) profile.getActivePunishments(PunishmentType.BLACKLIST).toArray()[0];
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, GeneralListener.getPunishmentMessage(punishment, punishment.isIP() ? loggedInIP : ""));
                saveLatestIP(profile, loggedInIP);
                return;
            }

            if (!profile.getCurrentIPAddress().equals(loggedInIP)) {
                List<Profile> altProfiles = BridgeGlobal.getMongoHandler().getProfiles(loggedInIP);

                for (Profile alt : altProfiles) {
                    if (alt.getActivePunishments(PunishmentType.BAN).size() > 0 || alt.getActivePunishments(PunishmentType.BLACKLIST).size() > 0) {
                        Punishment punishment = (Punishment) profile.getActivePunishments(PunishmentType.BAN).toArray()[0];
                        if (punishment == null)
                            punishment = (Punishment) profile.getActivePunishments(PunishmentType.BLACKLIST).toArray()[0];

                        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, GeneralListener.getPunishmentMessage(punishment, punishment.isIP() ? loggedInIP : ""));
                        return;
                    }
                }
            }
        }

        BridgeGlobal.getProfileHandler().addProfile(profile);
        profile.setCurrentIPAddress(loggedInIP);
        profile.getPreviousIPAddresses().add(loggedInIP);
    }

    private void saveLatestIP(Profile profile, String loggedInIP) {
        profile.setCurrentIPAddress(loggedInIP);
        profile.getPreviousIPAddresses().add(loggedInIP);
        profile.saveProfile();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(e.getPlayer().getUniqueId());

        if (profile != null) {
            profile.refreshCurrentGrant();
            FrozenNametagHandler.reloadPlayer(p);
        }

        Bukkit.getScheduler().runTaskAsynchronously(Bridge.getInstance(), () -> {

            if (profile != null) {
                if (profile.getFirstJoined() == 0) profile.setFirstJoined(System.currentTimeMillis());
                profile.setLastJoined(System.currentTimeMillis());
                profile.setUsername(p.getName());
                profile.saveProfile();

                BridgeGlobal.getDisguiseManager().load(profile.getUsername(), profile.getUuid());

                Runnable runnable = () -> {
                    DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(profile.getUuid());

                    if (disguisePlayer != null) {
                        if (p.hasPermission("bridge.disguise")) {
                            try {
                                if (BridgeGlobal.getDisguiseManager().disguise(p, disguisePlayer, null, true, true, false)) {
                                    p.sendMessage(org.bukkit.ChatColor.GREEN + "");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Msg.logConsole(ChatColor.RED + "Failed to disguise " + p.getName() + "!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Since you don't have disguise permission your disguised profile has been removed.");
                            BridgeGlobal.getDisguiseManager().save(p.getUniqueId(), false);
                        }
                    }
                };

                if (FrozenTabHandler.getLayoutProvider() != null) {
                    Tasks.runAsyncLater(runnable, 10L);
                } else {
                    runnable.run();
                }

                List<Profile> altProfiles = BridgeGlobal.getMongoHandler().getProfiles(profile.getCurrentIPAddress());
                List<String> formattedName = new ArrayList<>();
                if (!altProfiles.isEmpty() && altProfiles.size() > 1) {
                    for (Profile alt : altProfiles) {
                        if (alt.getUuid().equals(profile.getUuid())) return;
                        if (Bukkit.getOfflinePlayer(alt.getUuid()).isOnline())
                            formattedName.add(ChatColor.GREEN + alt.getUsername());
                        else if (alt.getActivePunishments(PunishmentType.BLACKLIST).size() > 1)
                            formattedName.add(ChatColor.DARK_RED + alt.getUsername());
                        else if (alt.getActivePunishments(PunishmentType.BAN).size() > 1)
                            formattedName.add(ChatColor.RED + alt.getUsername());
                        else formattedName.add(ChatColor.GRAY + alt.getUsername());
                    }
                    GeneralListener.broadcastMessage("&6&l[Alts] " + BukkitAPI.getColor(profile) + p.getName() + " &eis possibly &cban evading &7(" + (formattedName.size()) + " accounts)" + "\n" + StringUtils.join(formattedName, ChatColor.WHITE + ", "), "bridge.alts.sendmessage");
                }
            }
//
//            if (p.hasPermission("op") && BridgeGlobal.getUpdaterManager().getFilesForGroup(Objects.requireNonNull(BridgeGlobal.getUpdaterGroups())).stream().anyMatch(file -> BridgeGlobal.getUpdaterManager().getStatus(file).isShouldUpdate())) {
//                p.sendMessage(ChatColor.LIGHT_PURPLE.toString() + (int) BridgeGlobal.getUpdaterManager().getFilesForGroup(BridgeGlobal.getUpdaterGroups()).stream().filter(file -> BridgeGlobal.getUpdaterManager().getStatus(file).isShouldUpdate()).count() + ChatColor.YELLOW + " update is available, install with " + ChatColor.LIGHT_PURPLE + "/updater update" + ChatColor.YELLOW + ".");
//                List<File> files = BridgeGlobal.getUpdaterManager().getFilesForGroup(BridgeGlobal.getUpdaterGroups());
//                files.forEach(file1 -> {
//                    UpdateStatus updateStatus = BridgeGlobal.getUpdaterManager().getStatus(file1);
//                    p.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + file1.getName() + " " + updateStatus.getPrefix());
//                });
//            }
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasMetadata("NoSpamCheck")) {
            return;
        }
        Profile profile = BukkitAPI.getProfile(e.getPlayer());
        if (profile == null) {
            e.setCancelled(true);
            return;
        }

        if (profile.isMuted()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You are currently muted.");
            e.getPlayer().sendMessage(ChatColor.RED + (profile.getMute().isPermanent() ? "This mute is permanent." : "Time remaining: " + profile.getMute().getRemainingString()));
            return;
        }

        Filter filter = BridgeGlobal.getFilterHandler().isViolatingFilter(ChatColor.stripColor(e.getMessage()));

        if (filter != null && !e.getPlayer().hasPermission("basic.staff")) {
            if (filter.getFilterAction() == FilterAction.MUTE)
                MuteCommand.muteCmd(Bukkit.getConsoleSender(), false, profile, filter.getMuteTime(), " \"" + e.getMessage() + "\"");
            FrozenXPacketHandler.sendToAll(new FilterViolationPacket(Bukkit.getServerName(), e.getPlayer().getName(), null, e.getMessage()));
            e.setCancelled(true);

            ChatPlayer chatPlayer = ChatHandler.getChatPlayer(e.getPlayer().getUniqueId());

            ChatPopulator chatPopulator = chatPlayer.getRegisteredPopulators().stream().filter(chatPopulator1 -> chatPopulator1.getChatChar() == e.getMessage().charAt(0)).findFirst().orElse(null);
            if (chatPopulator == null) chatPopulator = chatPlayer.getSelectedPopulator();

            e.getPlayer().sendMessage(chatPopulator.layout(e.getPlayer(), e.getMessage()));
            return;
        }

        Rank rank = BukkitAPI.getPlayerRank(e.getPlayer());
        e.setMessage(rank.isStaff() ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage());
        e.setFormat(BukkitAPI.getColor(e.getPlayer()) + BukkitAPI.getPrefix(e.getPlayer()) + e.getPlayer().getName() + BukkitAPI.getSuffix(e.getPlayer()) + "§r: %2$s");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(e.getPlayer().getUniqueId());

        if (profile != null) {
            profile.setLastQuit(System.currentTimeMillis());
            profile.setConnectedServer(BridgeGlobal.getSystemName());
            profile.saveProfile();
            BridgeGlobal.getProfileHandler().getProfiles().remove(profile);
        }

        BridgeGlobal.getDisguiseManager().leave(e.getPlayer().getUniqueId());
    }

}
