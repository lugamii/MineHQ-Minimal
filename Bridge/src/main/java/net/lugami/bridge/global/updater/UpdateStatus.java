package net.lugami.bridge.global.updater;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum UpdateStatus {


    NOT_INSTALLED("Not Installed", "§b[Not Installed]", true),
    NEW_UPDATE("New Update", "§9[New Update]", true),
    LATEST("Latest", "§a[Latest]", false),
    ERROR("Error", "§4§l[ERROR!]", false);

    private String name;
    private String prefix;
    private boolean shouldUpdate;
}
