package net.lugami.bridge.bukkit.commands.grant.events;

import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.bukkit.util.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class GrantUpdateEvent extends BaseEvent {

    private UUID uuid;
    private Grant grant;

}
