package net.lugami.bridge.global.profile;

import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.handlers.MongoHandler;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.SystemType;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.GlobalAPI;
import lombok.Getter;
import lombok.Setter;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import net.lugami.bridge.global.disguise.DisguisePlayer;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Profile {

    @Getter
    private static Profile consoleProfile = new Profile(UUID.fromString("00000000-0000-0000-0000-000000000000"), "Console", "§8[§4§lCONSOLE§8] §4", "§f", "§4§l");

    private UUID uuid;
    private String username, prefix = "", suffix = "", color = "", connectedServer;
    private List<Grant> grants;
    private Rank tagRank = null;
    private List<String> activePermissions;
    private HashMap<String, String> permissions;
    private boolean debug = false;
    private long firstJoined = System.currentTimeMillis(), lastJoined = System.currentTimeMillis(), lastQuit = System.currentTimeMillis();
    private Map<String, String> metaData;

    private String currentIPAddress;
    private HashSet<String> previousIPAddresses;
    private HashSet<UUID> bypassPunishmentAccounts;
    private HashSet<Punishment> punishments;
    private HashSet<Punishment> staffPunishments;

    private boolean totpRequired;
    private String secretKey = "";

    private long becameStaffOn; // Still active
    private long returnedOn; // Returned
    private long removedStaffOn; // Resigned

    public Profile(String username, UUID uuid, boolean load) {
        this.username = username;
        this.uuid = uuid;
        this.grants = new ArrayList<>();
        this.permissions = new HashMap<>();
        this.activePermissions = new ArrayList<>();
        this.metaData = new HashMap<>();
        this.previousIPAddresses = new HashSet<>();
        this.bypassPunishmentAccounts = new HashSet<>();
        this.punishments = new HashSet<>();
        this.staffPunishments = new HashSet<>();
        this.totpRequired = false;

        if (load) {
            BridgeGlobal.getMongoHandler().loadProfile(this.uuid.toString(), profile -> {
                if (profile != null) {
                    this.importSettings(profile);
                }
            }, true, MongoHandler.LoadType.UUID);
        }
    }

    public Profile(UUID uuid, String username, String prefix, String suffix, String color) {
        this.uuid = uuid;
        this.username = username;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    public void importSettings(Profile profile) {
        this.grants = profile.getGrants();
        this.permissions = profile.getPermissions();

        this.firstJoined = profile.getFirstJoined();
        this.lastJoined = profile.getLastJoined();
        this.lastQuit = profile.getLastQuit();

        this.connectedServer = profile.getConnectedServer();
        this.metaData = profile.getMetaData();

        this.currentIPAddress = profile.getCurrentIPAddress();
        this.previousIPAddresses = profile.getPreviousIPAddresses();
        this.bypassPunishmentAccounts = profile.getBypassPunishmentAccounts();
        this.punishments = profile.getPunishments();
        this.staffPunishments = profile.getStaffPunishments();

        this.secretKey = profile.getSecretKey();
        this.becameStaffOn = profile.getBecameStaffOn();
        this.removedStaffOn = profile.getRemovedStaffOn();
        refreshCurrentGrant();
    }

    public void checkTotpLock(UUID uuid, String ip) {
        Profile p = BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(uuid);
        if (getCurrentGrant().getRank().isStaff() && !p.currentIPAddress.equals(ip)) {
            this.totpRequired = true;
        } else {
            this.totpRequired = false;
        }

    }

    public void saveProfile() {
        BridgeGlobal.getMongoHandler().saveProfile(this, callback -> {
            if (callback) {
                BridgeGlobal.sendLog("§aSuccessfully saved §f" + getUsername() + "§a.");
            } else {
                BridgeGlobal.sendLog("§cFailed to save §f" + getUsername() + "§c.");
            }
        }, true);
    }

    public List<Grant> getActiveGrants() {
        List<Grant> active = new ArrayList<>();

        if (grants == null) // Fix console NPE
            return active;

        grants.removeIf(grant -> grant.getRank() == null || GlobalAPI.getRank(grant.getRank().getUuid()) == null);
        for (Grant grant : grants) {

            if (grant.isStillActive() && grant.isGrantActiveOnScope()) {
                active.add(grant);
            }
        }
        active.sort(Comparator.comparingInt(o -> o.getRank().getPriority()));
        return active;
    }

    public Grant getCurrentGrant() {
        Grant grant = null;

        for (Grant activeGrant : getActiveGrants()) {
            if (grant == null) {
                grant = activeGrant;
                continue;
            }
            if (activeGrant.getRank().getPriority() > grant.getRank().getPriority()) {
                grant = activeGrant;
            }
        }

        if (grant == null) {
            applyGrant(Grant.getDefaultGrant(), null, false);
            for (Grant activeGrant : getActiveGrants()) {
                if (grant == null) {
                    grant = activeGrant;
                    continue;
                }
                if (activeGrant.getRank().getPriority() > grant.getRank().getPriority()) {
                    grant = activeGrant;
                }
            }
        }

        return grant;
    }


    public Rank getDisguisedRank() {
        DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(this.uuid);
        return disguisePlayer != null ? disguisePlayer.getDisguiseRank() : null;
    }

    public DisguisePlayer getDisguise() {
        return BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(this.uuid);
    }

    public boolean isOnline() {
        return BridgeGlobal.getServerHandler().findPlayerServer(getUuid()) != null;
    }

    public String getConnectedServer() {
        return (BridgeGlobal.getServerHandler().findPlayerServer(getUuid()) == null ? (this.connectedServer == null ? "N/A" : this.connectedServer) : BridgeGlobal.getServerHandler().findPlayerServer(getUuid()).getName());
    }

    public void applyGrant(Grant grant, UUID executor, boolean shouldGetCurrentGrant) {
        if (grants == null) return; // Fix console NPE

        if (grant.getRank().isStaff() && getBecameStaffOn() == 0) becameStaffOn = grant.getInitialTime();

        grants.add(grant);
        if (shouldGetCurrentGrant && getCurrentGrant().getUuid().toString().equalsIgnoreCase(grant.getUuid().toString())) {
            refreshCurrentGrant();
        }
        BridgeGlobal.sendLog("Successfully applied " + getUsername() + "'s Grant of the " + grant.getRank().getName() + " Rank");
    }

    public void refreshCurrentGrant() {
        if (BridgeGlobal.getSystemType() == SystemType.BUKKIT) GeneralListener.updatePermissions(getUuid());
    }

    public void updateColor() {
        GeneralListener.updateColor(getUuid());
    }

    public void applyGrant(Grant grant, UUID executor) {
        applyGrant(grant, executor, true);
    }

    public boolean hasGrantOf(Rank rank, boolean onScope) {
        if (onScope)
            return getGrants().stream().filter(grant -> grant.getUuid().toString().equalsIgnoreCase(rank.getUuid().toString()) && grant.isGrantActiveOnScope()).findFirst().orElse(null) != null;
        return getGrants().stream().filter(grant -> grant.getUuid().toString().equalsIgnoreCase(rank.getUuid().toString())).findFirst().orElse(null) != null;
    }

    public boolean hasGrantOf(Rank rank) {
        return hasGrantOf(rank, false);
    }

    public boolean hasActiveGrantOf(Rank rank) {
        return getActiveGrants().stream().filter(grant -> grant.getUuid().toString().equalsIgnoreCase(rank.getUuid().toString())).findFirst().orElse(null) != null;
    }

    /*
            Punishments
     */

    public Punishment getPunishmentByID(UUID id) {
        return punishments.stream().filter(punishment -> punishment.getUuid().toString().equals(id.toString())).findFirst().orElse(null);
    }

    public Set<Punishment> getActivePunishments() {
        return this.punishments.stream().filter(Punishment::isActive).collect(Collectors.toSet());
    }

    public Set<Punishment> getActivePunishments(PunishmentType punishmentType) {
        return this.punishments.stream().filter(punishment -> punishment.getPunishmentType() == punishmentType && punishment.isActive()).collect(Collectors.toSet());
    }

    public Set<Punishment> getAltActivePunishments(PunishmentType punishmentType) {
        Set<Punishment> punishments = new HashSet<>();
        if (currentIPAddress == null) return punishments;

        for (Profile alt : BridgeGlobal.getMongoHandler().getProfiles(currentIPAddress)) {
            punishments.addAll(alt.getActivePunishments());
        }

        return punishments;

    }

    public Set<Punishment> getPunishments(PunishmentType punishmentType) {
        return this.punishments.stream().filter(punishment -> punishment.getPunishmentType() == punishmentType).collect(Collectors.toSet());
    }

    public boolean isMuted() {
        return getActivePunishments(PunishmentType.MUTE).size() > 0;
    }

    public Punishment getMute() {
        return (Punishment) getActivePunishments(PunishmentType.MUTE).toArray()[0];
    }

    public boolean hasPermission(String str) {
        return hasPermission(str, BridgeGlobal.getServerName()) || hasPermission(str, "GR|" + BridgeGlobal.getGroupName());
    }

    public boolean hasPermission(String str, String serverOrGroup) {
        if (getPermissions().containsKey("*") && (getPermissions().get("*").equalsIgnoreCase("Global") || getPermissions().get("*").equalsIgnoreCase(serverOrGroup)))
            return true;
        return getActiveGrants().stream().filter(grant -> grant.isGrantActiveOnScope() && grant.isStillActive()).anyMatch(grant -> grant.getRank().hasPermission(str, serverOrGroup)) || getPermissions().containsKey(str) && (getPermissions().get(str).equalsIgnoreCase(serverOrGroup) || getPermissions().get(str).equalsIgnoreCase("Global"));
    }

    public boolean togglePerm(String str, String serverOrGroup) {
        if (hasPermission(str, serverOrGroup)) {
            getPermissions().remove(str);
            return false;
        } else {
            getPermissions().put(str, serverOrGroup);
            return true;
        }
    }
}
