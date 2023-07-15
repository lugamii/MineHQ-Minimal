package net.lugami.qlib.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter @AllArgsConstructor @RequiredArgsConstructor
public final class NametagUpdate {

    private final UUID toRefresh;
    private UUID refreshFor;
}
