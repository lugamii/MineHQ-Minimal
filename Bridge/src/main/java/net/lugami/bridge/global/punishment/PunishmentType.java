package net.lugami.bridge.global.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum PunishmentType {

    WARN("Warn", "warned", "unwarned"),
    KICK("Kick", "kicked", null),
    MUTE("Mute", "muted", "unmuted"),
    BAN("Ban", "banned", "unbanned"),
    BLACKLIST("Blacklist", "blacklisted", "unblacklisted");

    private String displayName;
    private String punishmentName;
    private String undoPunishmentName;

}
