package net.lugami.qlib.command;

import java.beans.ConstructorProperties;
import java.util.Set;

public class ParameterData implements Data {

    private final String name;
    private final String defaultValue;
    private final String extraData;
    private final Class<?> type;
    private final boolean wildcard;
    private final int methodIndex;
    private final Set<String> tabCompleteFlags;
    private final Class<? extends ParameterType<?>> parameterType;

    @ConstructorProperties(value={"name", "defaultValue", "extraData", "type", "wildcard", "methodIndex", "tabCompleteFlags", "parameterType"})
    public ParameterData(String name, String defaultValue, String extraData, Class<?> type, boolean wildcard, int methodIndex, Set<String> tabCompleteFlags, Class<? extends ParameterType<?>> parameterType) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.extraData = extraData;
        this.type = type;
        this.wildcard = wildcard;
        this.methodIndex = methodIndex;
        this.tabCompleteFlags = tabCompleteFlags;
        this.parameterType = parameterType;
    }

    public String getName() {
        return this.name;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getExtraData() {
        return this.extraData;
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isWildcard() {
        return this.wildcard;
    }

    public int getMethodIndex() {
        return this.methodIndex;
    }

    public Set<String> getTabCompleteFlags() {
        return this.tabCompleteFlags;
    }

    public Class<? extends ParameterType<?>> getParameterType() {
        return this.parameterType;
    }
}

