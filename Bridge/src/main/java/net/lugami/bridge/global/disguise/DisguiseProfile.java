package net.lugami.bridge.global.disguise;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DisguiseProfile {

    private final String name;
    private String displayName;
    private String skinName;
    private DisguisePlayerSkin skin;
}
