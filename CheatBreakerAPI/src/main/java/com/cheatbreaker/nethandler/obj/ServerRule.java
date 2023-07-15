package com.cheatbreaker.nethandler.obj;

import lombok.Getter;

@Getter
public enum ServerRule {

    VOICE_ENABLED("voiceEnabled", Boolean.class),
    MINIMAP_STATUS("minimapStatus", String.class),
    SERVER_HANDLES_WAYPOINTS("serverHandlesWaypoints", Boolean.class),
    COMPETITIVE_GAMEMODE("competitiveGame", Boolean.class);

    private final String rule;
    private final Class value;

    public static ServerRule getRule(String name) {
        ServerRule rule = null;
        for (ServerRule sr : ServerRule.values()) {
            if (!sr.getRule().equals(name)) continue;
            rule = sr;
        }
        return rule;
    }

    ServerRule(String rule, Class value) {
        this.rule = rule;
        this.value = value;
    }

}