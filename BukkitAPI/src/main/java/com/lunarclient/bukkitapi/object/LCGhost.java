package com.lunarclient.bukkitapi.object;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

public final class LCGhost {

    @Getter private final List<UUID> ghostedPlayers;
    @Getter private final List<UUID> unGhostedPlayers;

    public LCGhost(List<UUID> ghostedPlayers, List<UUID> unGhostedPlayers) {
        this.ghostedPlayers = ghostedPlayers;
        this.unGhostedPlayers = unGhostedPlayers;
    }
}
