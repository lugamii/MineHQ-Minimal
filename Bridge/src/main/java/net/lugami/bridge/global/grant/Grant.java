package net.lugami.bridge.global.grant;

import net.lugami.bridge.global.util.TimeUtil;
import net.lugami.qlib.util.TimeUtils;
import net.md_5.bungee.api.ChatColor;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.util.JsonChain;
import net.lugami.bridge.global.util.OtherUtils;
import net.lugami.bridge.global.ranks.Rank;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Getter
public class Grant {

    @Getter private static final Grant defaultGrant = new Grant(BridgeGlobal.getRankHandler().getDefaultRank(), Long.MAX_VALUE, Collections.singletonList("Global"), "", "N/A", BridgeGlobal.getSystemName());

    private final UUID uuid;
    private final Rank rank;
    private final long length;
    private final long initialTime;
    private final List<String> scope;
    private final String reason;
    private final String grantedBy;
    private final String grantedOn;

    @Setter private boolean removed;
    @Setter private long removedAt;
    @Setter private String removedReason, removedBy, removedOn;

    public Grant(Rank rank, long length, List<String> scope, String reason, String grantedBy, String grantedOn) {
        this.uuid = UUID.randomUUID();
        this.rank = rank;
        this.scope = scope;
        this.length = length;
        this.initialTime = System.currentTimeMillis();
        this.reason = reason;
        this.grantedBy = grantedBy;
        this.grantedOn = grantedOn;
        this.removed = false;
        this.removedAt = -1L;
        this.removedBy = "";
        this.removedOn = "";
        this.removedReason = "";
    }

    public Grant(UUID uuid, Rank rank, long length, long initialTime, List<String> scope, String reason, String grantedBy, String grantedOn) {
        this.uuid = uuid;
        this.rank = rank;
        this.length = length;
        this.initialTime = initialTime;
        this.scope = scope;
        this.reason = reason;
        this.grantedBy = grantedBy;
        this.grantedOn = grantedOn;
    }

    public Grant(UUID uuid, Rank rank, long length, long initialTime, List<String> scope, String reason, String grantedBy, String grantedOn, boolean removed, long removedAt, String removedReason, String removedBy, String removedOn) {
        this.uuid = uuid;
        this.rank = rank;
        this.length = length;
        this.initialTime = initialTime;
        this.scope = scope;
        this.reason = reason;
        this.grantedBy = grantedBy;
        this.grantedOn = grantedOn;
        this.removed = removed;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
        this.removedBy = removedBy;
        this.removedOn = removedOn;
    }

    public static Grant deserialize(String grant) {
        JsonObject object = new JsonParser().parse(grant).getAsJsonObject();
        return new Grant(
                UUID.fromString(object.get("uuid").getAsString()),
                BridgeGlobal.getRankHandler().getRankByID(UUID.fromString(object.get("rank").getAsString())),
                object.get("length").getAsLong(),
                object.get("initialTime").getAsLong(),
                new ArrayList<>(Arrays.asList(object.get("scope").getAsString().split(","))),
                object.get("reason").getAsString(),
                object.get("grantedBy").getAsString(),
                object.get("grantedOn").getAsString(),
                object.get("removed").getAsBoolean(),
                object.get("removedAt").getAsLong(),
                object.get("removedBy").getAsString(),
                object.get("removedOn").getAsString(),
                object.get("removedReason").getAsString());
    }

    public static JsonObject serialize(Grant grant) {

        return new JsonChain()
                .addProperty("uuid", grant.getUuid().toString())
                .addProperty("rank", grant.getRank().getUuid().toString())
                .addProperty("length", grant.getLength())
                .addProperty("initialTime", grant.getInitialTime())
                .addProperty("scope", StringUtils.join(grant.getScope(), ','))
                .addProperty("reason", grant.getReason())
                .addProperty("grantedBy", grant.grantedBy)
                .addProperty("grantedOn", grant.getGrantedOn())
                .addProperty("removed", grant.isRemoved())
                .addProperty("removedAt", grant.getRemovedAt())
                .addProperty("removedReason", grant.getRemovedReason())
                .addProperty("removedBy", grant.removedBy)
                .addProperty("removedOn", grant.getRemovedOn())
                .get();
    }

    public String formatGrantedTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mmaa");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return dateFormat.format(cal.getTime()) + " (" + TimeUtil.getTimeZoneShortName(cal.getTimeZone().getDisplayName()) + ")";
    }

    public long getActiveUntil() {
        return length == Long.MAX_VALUE ? Long.MAX_VALUE : (initialTime + length);
    }

    public boolean isStillActive() {
        return getActiveUntil() > System.currentTimeMillis() && !removed;
    }

    public String getGrantedBy() {
        if(OtherUtils.isUUID(grantedBy)) {
            Profile pf = BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(UUID.fromString(grantedBy));
            return (pf == Profile.getConsoleProfile() ? pf.getColor() : pf.getCurrentGrant().getRank().getColor()) + pf.getUsername();
        }else {
            return grantedBy;
        }
    }

    public String getGrantedByColorless() {
        if(OtherUtils.isUUID(grantedBy)) {
            Profile pf = BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(UUID.fromString(grantedBy));
            return (pf == Profile.getConsoleProfile() ? ChatColor.LIGHT_PURPLE : ChatColor.LIGHT_PURPLE) + pf.getUsername();
        }else {
            return grantedBy;
        }
    }

    public String getRemovedBy() {
        if(OtherUtils.isUUID(removedBy)) {
            Profile pf = BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(UUID.fromString(removedBy));
            return (pf == Profile.getConsoleProfile() ? pf.getColor(): pf.getCurrentGrant().getRank().getColor() ) + pf.getUsername();
        }else {
            return removedBy;
        }
    }


    public boolean isGrantActiveOnScope() {
        switch (BridgeGlobal.getSystemType()) {
            case BUNGEE: {
                return getScope().stream().anyMatch(s -> (s.equalsIgnoreCase("global") || s.equalsIgnoreCase("bungeecord") || s.equalsIgnoreCase("gr-bungeecord")));
            }
            case BUKKIT: {
                return getScope().stream().anyMatch(s -> (s.equalsIgnoreCase("global") || s.equalsIgnoreCase(BridgeGlobal.getSystemName()) || s.equalsIgnoreCase("gr-" + BridgeGlobal.getFromConfig("servergroup"))));
            }
            default: {
                return getScope().contains("Global");
            }
        }
    }

    public String getDate() {
        return TimeUtils.formatIntoCalendarStringNoTime(new Date(getInitialTime()));
    }

}
