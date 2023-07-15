package net.lugami.bridge.global.punishment;

import com.google.gson.Gson;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.lugami.bridge.bukkit.BukkitAPI;

import java.util.HashSet;
import java.util.UUID;

@Getter @Setter
public class Punishment {

    private UUID uuid;
    private UUID target, executor, pardonedBy;
    private PunishmentType punishmentType;
    private boolean pardoned, isIP, silent, clear;
    private String punishedServer, reason, pardonedServer, pardonedReason;
    private long time, duration, pardonedAt;
    private HashSet<Evidence> proof;

    public Punishment(Profile target, Profile executor, String punishedServer, String reason, PunishmentType punishmentType,HashSet<Evidence> proof, boolean isIP, boolean silent, boolean clear, long duration) {
        this.uuid = UUID.randomUUID();
        this.target = target.getUuid();
        this.executor = executor.getUuid();
        this.punishedServer = punishedServer;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.isIP = isIP;
        this.silent = silent;
        this.clear = clear;
        this.time = System.currentTimeMillis();
        this.duration = duration;
        this.proof = proof;
    }

    public Punishment(UUID uuid, Profile target, Profile executor, Profile pardonedBy, PunishmentType punishmentType, HashSet<Evidence> proof, boolean pardoned, boolean isIP, boolean silent, boolean clear, String punishedServer, String reason, String pardonedServer, String pardonedReason, long time, long duration, long pardonedAt,  Profile profile) {
        this.uuid = uuid;
        this.target = target.getUuid();
        this.executor = executor.getUuid();
        this.pardonedBy = pardonedBy.getUuid();
        this.punishmentType = punishmentType;
        this.pardoned = pardoned;
        this.isIP = isIP;
        this.silent = silent;
        this.clear = clear;
        this.punishedServer = punishedServer;
        this.reason = reason;
        this.pardonedServer = pardonedServer;
        this.pardonedReason = pardonedReason;
        this.time = time;
        this.duration = duration;
        this.pardonedAt = pardonedAt;
        this.proof = proof;
    }

    public Punishment(UUID uuid, Profile target, Profile executor, Profile profile, PunishmentType punishmentType, Boolean pardoned, Boolean isIP, Boolean silent, Boolean clear, String punishedServer, String reason, String pardonedServer, String pardonedReason, Long time, Long duration, Long pardonedAt) {
    }


    public boolean isPermanent() {
        return this.punishmentType == PunishmentType.BLACKLIST || this.duration == Long.MAX_VALUE;
    }

    public long getRemainingTime() {
        return System.currentTimeMillis() - (this.time + this.duration);
    }

    public boolean isActive() {
        return !this.pardoned && (this.isPermanent() || this.getRemainingTime() < 0L);
    }

    public void pardon(Profile pardonedBy, String pardonedServer, String pardonedReason, boolean silent) {
        this.pardoned = true;
        this.pardonedAt = System.currentTimeMillis();
        this.pardonedBy = pardonedBy.getUuid();
        this.pardonedServer = pardonedServer;
        this.pardonedReason = pardonedReason;
        this.silent = silent;
    }

    public String getRemainingString() {
        if (this.pardoned) {
            return "Pardoned";
        }
        if (this.isPermanent()) {
            return "Permanent";
        }
        if (!this.isActive()) {
            return "Expired";
        }
        return TimeUtils.formatIntoDetailedString((int) ((this.time + this.duration - System.currentTimeMillis()) / 1000));
    }

    public String getStatusString() {
        if (this.pardoned) {
            return "Pardoned";
        }
        if (this.isPermanent()) {
            return "Permanent";
        }
        if (!this.isActive()) {
            return "Expired";
        }
        return "Active";
    }

    public String getDurationString() {
        if (this.isPermanent()) {
            return "Permanent";
        }
        return TimeUtil.millisToRoundedTime(this.duration);
    }

    public String getDisplayName() {
        if (this.punishmentType != PunishmentType.BAN && this.punishmentType != PunishmentType.MUTE) {
            return this.pardoned ? this.punishmentType.getUndoPunishmentName() : this.punishmentType.getPunishmentName();
        }
        if (this.isPermanent()) {
            return this.pardoned ? this.punishmentType.getUndoPunishmentName() : (this.punishmentType.getPunishmentName());
        }
        return this.pardoned ? this.punishmentType.getUndoPunishmentName() : ("temporarily " + this.punishmentType.getPunishmentName());
    }

    public Profile getTarget() {
        return BukkitAPI.getProfile(target);
    }

    public Profile getExecutor() {
        return BukkitAPI.getProfile(executor);
    }

    public Profile getPardonedBy() {
        return BukkitAPI.getProfile(pardonedBy);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
