package net.lugami.bridge.global.filter;

import net.lugami.bridge.BridgeGlobal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public class Filter {

    private final UUID uuid;
    private final FilterType filterType;
    private final FilterAction filterAction;
    private final String pattern;
    private final long muteTime;

    public Filter(FilterType filterType, String pattern) {
        this.uuid = UUID.randomUUID();
        this.filterType = filterType;
        this.filterAction = FilterAction.HIDE;
        this.pattern = pattern;
        this.muteTime = -1L;
    }

    public Filter(FilterType filterType, String pattern, long muteTime) {
        this.uuid = UUID.randomUUID();
        this.filterType = filterType;
        this.filterAction = FilterAction.MUTE;
        this.pattern = pattern;
        this.muteTime = muteTime;
    }

    public boolean isViolatingFilter(String message) {
        if(getFilterType() == FilterType.REGEX) {
            return Pattern.compile(getPattern(), Pattern.CASE_INSENSITIVE).matcher(message).find();
        }
        return message.toLowerCase().contains(getPattern().toLowerCase());
    }

    public void save() {
        BridgeGlobal.getMongoHandler().saveFilter(this, callback -> {
            if (callback) {
                BridgeGlobal.sendLog("§aSuccessfully saved the filter §f" + getUuid() + "§a.");
            } else {
                BridgeGlobal.sendLog("§cFailed to save the filter §f" + getUuid() + "§c.");
            }
        }, true);
    }

    public void delete() {
        BridgeGlobal.getMongoHandler().removeFilter(getUuid(), callback -> {
            if (callback) {
                BridgeGlobal.sendLog("§aSuccessfully removed the filter §f" +getUuid() + "§a.");
                BridgeGlobal.getFilterHandler().getFilters().remove(this);
            } else {
                BridgeGlobal.sendLog("§cFailed to remove the filter §f" + getUuid() + "§c.");
            }
        }, true);
    }

}
