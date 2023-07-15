package net.lugami.bridge.global.handlers;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.filter.Filter;
import lombok.Getter;

import java.util.*;

public class FilterHandler {

    @Getter private final Set<Filter> filters = new HashSet<>();

    public void init() {
        filters.clear();

        List<Filter> filterList = new ArrayList<>();
        try {
            BridgeGlobal.getMongoHandler().getFiltersInDB(callback-> {

                BridgeGlobal.sendLog("§aFound " + callback.size() + " filters in the database.");


                for (int i = 0; i < callback.size(); i++) {
                    UUID uuid = callback.get(i);
                    BridgeGlobal.getMongoHandler().loadFilter(uuid, cback -> {
                        if (cback == null) {
                            System.out.println("Welp. thats a null.");
                            return;
                        }

                        filterList.add(cback);
                    }, false);

                    if(callback.size() == filterList.size()) {
                        filters.addAll(filterList);
                        BridgeGlobal.sendLog("§aLoaded all filters.");
                    }
                }

            });

        } catch (Exception ex) {
            BridgeGlobal.sendLog("§cFailed to initialize the filter manager.");
            ex.printStackTrace();
            BridgeGlobal.sendLog(ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    public Filter getFilter(UUID uuid) {
        return filters.stream().filter(filter -> filter.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public Filter getFilter(String pattern) {
        return filters.stream().filter(filter -> filter.getPattern().equalsIgnoreCase(pattern)).findFirst().orElse(null);
    }

    public Filter addFilter(Filter filter) {
        filters.stream().filter(filter1 -> filter1.getUuid().equals(filter.getUuid())).findFirst().ifPresent(filters::remove);
        filters.add(filter);
        return filter;
    }

    public boolean removeFilter(Filter filter) {
        return filters.remove(filter);
    }

    public Filter isViolatingFilter(String message) {
        return filters.stream().filter(filter -> filter.isViolatingFilter(message)).findFirst().orElse(null);
    }

}
