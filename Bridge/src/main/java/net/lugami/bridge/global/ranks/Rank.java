package net.lugami.bridge.global.ranks;

import net.lugami.bridge.global.util.SystemType;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.GlobalAPI;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter @Setter
public class Rank {

    private UUID uuid;
    private String name = "", prefix = "§f", suffix = "§f", color = "§f", displayName = "";
    private int priority = 0;
    private boolean staff = false, media = false, builder = false, hidden = false, grantable = true, defaultRank = false;
    private ArrayList<UUID> inherits;
    private Map<String, String> permissions, metaData;
    @Getter
    private static List<Rank> ranks = new ArrayList<>();

    public Rank(UUID id, boolean imp) {
        this(id, imp, callback -> {});
    }

    public Rank(UUID id, boolean imp, Consumer<Boolean> callback) {
        this.uuid = id;
        this.permissions = new HashMap<>();
        this.metaData = new HashMap<>();
        this.inherits = new ArrayList<>();
        if (imp) {
            BridgeGlobal.getMongoHandler().loadRank(this.uuid, rank -> {
                if (rank != null) {
                    this.importSettings(rank);
                    callback.accept(true);
                } else {
                    callback.accept(false);
                }
            }, true);
        } else {
            callback.accept(true);
        }
    }

    public Rank(UUID id, String name, boolean imp) {
        this.uuid = id;
        this.name = name;
        this.displayName = name;
        this.permissions = new HashMap<>();
        this.metaData = new HashMap<>();
        this.inherits = new ArrayList<>();
        if (imp) {
            BridgeGlobal.getMongoHandler().loadRank(this.uuid, rank -> {
                if (rank != null) {
                    this.importSettings(rank);
                }
            }, true);
        }
    }


    public Rank(UUID id, String name, String prefix, String suffix, String displayName, int priority, boolean staff, boolean media, boolean builder, boolean hidden, boolean grantable, boolean defaultRank, String color) {
        this.uuid = id;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.priority = priority;
        this.staff = staff;
        this.media = media;
        this.builder = builder;
        this.hidden = hidden;
        this.grantable = grantable;
        this.displayName = displayName;
        this.defaultRank = defaultRank;
        this.color = color;
        this.permissions = new HashMap<>();
        this.metaData = new HashMap<>();
        this.inherits = new ArrayList<>();
    }

    public void load() {

    }

    public void importSettings(Rank rank) {
        if (rank == null) return;
        this.name = rank.getName();
        this.prefix = rank.getPrefix();
        this.suffix = rank.getSuffix();
        this.priority = rank.getPriority();
        this.staff = rank.isStaff();
        this.media = rank.isMedia();
        this.media = rank.isBuilder();
        this.hidden = rank.isHidden();
        this.grantable = rank.isGrantable();
        this.displayName = rank.getDisplayName();
        this.defaultRank = rank.isDefaultRank();
        this.color = rank.getColor();
        this.permissions = rank.getPermissions();
        this.metaData = rank.getMetaData();
        this.inherits = rank.inherits;
    }

    public void removeRank() {

        BridgeGlobal.getMongoHandler().removeRank(this.getUuid(), callback -> {
            if(callback != null) {
                BridgeGlobal.sendLog("§aSuccessfully deleted rank §r" + this.getColor() + this.getName() + "§a.");
                for (Profile profile : BridgeGlobal.getProfileHandler().getProfiles()) {
                    profile.getGrants().removeIf(grant -> grant.getRank() == null);
                    profile.refreshCurrentGrant();
                }
            }else {
                BridgeGlobal.sendLog("§cFailed to delete rank §r" + this.getColor() + this.getName() + "§a.");
            }
        }, true);

    }

    public boolean hasPermission(String str, String serverOrGroup) {
        if(getPermissions().containsKey("*") && (getPermissions().get("*").equalsIgnoreCase("Global") || getPermissions().get("*").equalsIgnoreCase(serverOrGroup))) return true;
        return getPermissions().containsKey(str) && (getPermissions().get(str).equalsIgnoreCase("Global") || getPermissions().get(str).equalsIgnoreCase(serverOrGroup) || getPermissions().containsKey(str)) || hasInheritPermission(str, serverOrGroup);
    }

    public boolean hasInheritPermission(String str, String serverOrGroup) {
        for (Rank inherit : getInherits()) {
            return inherit.hasPermission(str, serverOrGroup);
        }
        return false;
    }

    public static Rank getRankByName(String name) {
        for (Rank rank : ranks) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }


    public boolean hasPermission(String str) {
        return hasPermission(str, BridgeGlobal.getServerName()) || hasPermission(str, "GR|" + BridgeGlobal.getGroupName());
    }

    public List<String> getActivePermissions() {
        return getPermissions().keySet().stream().filter(str -> getPermissions().get(str).equalsIgnoreCase("Global") || getPermissions().get(str).equalsIgnoreCase("GR|" + BridgeGlobal.getGroupName()) || getPermissions().get(str).equalsIgnoreCase(BridgeGlobal.getServerName())).collect(Collectors.toList());
    }

    public boolean hasInherit(Rank inhr) {
        return inherits.contains(inhr.getUuid());
    }

    public List<Rank> getInherits() {
        return inherits.stream().map(GlobalAPI::getRank).collect(Collectors.toList());
    }

    public boolean togglePerm(String str, String serverOrGroup) {
        if(hasPermission(str, serverOrGroup)) {
            getPermissions().remove(str);
            return false;
        }else {
            getPermissions().put(str, serverOrGroup);
            return true;
        }
    }

    public boolean toggleInherit(Rank inhr) {
        if(hasInherit(inhr)) {
            inherits.remove(inhr.getUuid());
            return false;
        }else {
            inherits.add(inhr.getUuid());
            return true;
        }
    }

    public void saveRank() {
        if(BridgeGlobal.getSystemType() == SystemType.BUKKIT) GeneralListener.refreshPlayersInRank(this);
        BridgeGlobal.getMongoHandler().saveRank(this, callback -> {
            if (callback != null) {
                BridgeGlobal.sendLog("§aSuccessfully saved rank §r" + this.getColor() + this.getName() + "§a.");
            } else {
                BridgeGlobal.sendLog("§cFailed to save rank §r" + this.getColor() + this.getName() + "§c.");
            }
        }, true);
    }

    public List<Profile> getOnlineProfilesInRank() {
        return BridgeGlobal.getProfileHandler().getProfiles().stream().filter(profile -> profile.getActiveGrants().stream().anyMatch(grant -> grant.isStillActive() && grant.getRank() == this)).collect(Collectors.toList());
    }

    public List<Profile> getProfilesInRank() {
        return BridgeGlobal.getMongoHandler().getProfiles().parallelStream().filter(profile -> profile.getActiveGrants().parallelStream().anyMatch(grant -> grant.isStillActive() && grant.getRank() == this)).collect(Collectors.toList());
    }

}
