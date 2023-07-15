package net.lugami.bridge.global.status;

import com.google.gson.JsonObject;
import net.lugami.bridge.BridgeGlobal;

import java.beans.ConstructorProperties;
import java.util.List;

public abstract class StatusProvider {

    private final String name;
    private final int weight;

    public abstract String serverName();
    public abstract String serverStatus();
    public abstract int online();
    public abstract int maximum();
    public abstract String motd();
    public abstract double tps();
    public abstract List<String> players();
    public abstract JsonObject dataPassthrough();

    @ConstructorProperties(value={"plugin", "name", "weight"})
    public StatusProvider(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }

    protected static final class DefaultStatusProvider
            extends StatusProvider {

        public DefaultStatusProvider() {
            super("Default Provider", 0);
        }

        @Override
        public String serverName() {
            return BridgeGlobal.getSystemName();
        }

        @Override
        public String serverStatus() {
            return "ONLINE";
        }

        @Override
        public int online() {
            return 0;
        }

        @Override
        public int maximum() {
            return 0;
        }

        @Override
        public String motd() {
            return "";
        }

        @Override
        public double tps() {
            return 0;
        }

        @Override
        public List<String> players() {
            return null;
        }

        @Override
        public JsonObject dataPassthrough() {
            return null;
        }
    }

}