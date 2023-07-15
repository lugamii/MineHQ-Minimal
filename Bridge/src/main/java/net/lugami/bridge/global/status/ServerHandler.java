package net.lugami.bridge.global.status;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import net.lugami.bridge.global.status.threads.StatusFetchThread;
import net.lugami.bridge.global.status.threads.StatusUpdateThread;
import net.lugami.bridge.BridgeGlobal;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerHandler {

    @Getter private final List<StatusProvider> providers = new ArrayList<>();
    @Getter private final Map<String, BridgeServer> servers = new ConcurrentHashMap<>();
    @Getter private boolean initiated = false;


    public void init() {
        Preconditions.checkState(!initiated);
        this.initiated = true;
        registerProvider(new StatusProvider.DefaultStatusProvider());
        servers.put(BridgeGlobal.getSystemName().toLowerCase(), new BridgeServer(BridgeGlobal.getSystemName(), this.providers.get(0).getName(), BridgeGlobal.getGroupName(), "ONLINE", BridgeGlobal.getSystemType().name(), System.currentTimeMillis(), BridgeGlobal.getStartTime(), 0, 0, 0.0, new ArrayList<>()));
        new StatusUpdateThread().start();
        new StatusFetchThread().start();
    }

    public void registerProvider(StatusProvider newProvider) {
        providers.add(newProvider);
        providers.sort((a, b) -> Ints.compare(b.getWeight(), a.getWeight()));
    }

    public StatusProvider getProvider(String name) {
        return this.providers.stream().filter(statusProvider -> statusProvider.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public StatusProvider getProvider() {
        return this.providers.get(0);
    }


    public BridgeServer getServer(String name) {
        return servers.get(name.toLowerCase());
    }

    public BridgeServer getServer() {
        return getServer(BridgeGlobal.getSystemName());
    }

    public void updateServer(String name, BridgeServer bridgeServer) {
        if(servers.containsKey(name.toLowerCase())) servers.replace(name.toLowerCase(), bridgeServer);
        else servers.put(name.toLowerCase(), bridgeServer);
    }

    public boolean doesGroupExist(String g) {
        return getServersInGroup(g) != null && !getServersInGroup(g).isEmpty();
    }

    public List<BridgeServer> getServersInGroup(String group) {
        return servers.values().stream().filter(bridgeServer -> bridgeServer.getGroup().equals(group)).collect(Collectors.toList());
    }

    public Set<String> getGroups() {
        Set<String> groups = new HashSet<>();
        servers.values().stream().filter(bridgeServer -> !bridgeServer.getGroup().equals("N/A")).forEach(bridgeServer -> groups.add(bridgeServer.getGroup()));
        if(groups.isEmpty()) return null;
        return groups;
    }

    public BridgeServer findPlayerProxy(UUID uuid) {
        return servers.values().stream().filter(bridgeServer -> bridgeServer.getSystemType().toLowerCase().contains("bungee") && bridgeServer.getUsers().contains(uuid.toString())).findFirst().orElse(null);
    }

    public BridgeServer findPlayerServer(UUID uuid) {
        return servers.values().stream().filter(bridgeServer -> bridgeServer.getSystemType().toLowerCase().contains("bukkit") && bridgeServer.getUsers().contains(uuid.toString())).findFirst().orElse(null);
    }

}