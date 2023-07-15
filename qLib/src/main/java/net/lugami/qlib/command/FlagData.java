package net.lugami.qlib.command;

import java.beans.ConstructorProperties;
import java.util.List;

public class FlagData implements Data {

    private final List<String> names;
    private final String description;
    private final boolean defaultValue;
    private final int methodIndex;

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    @ConstructorProperties(value={"names", "description", "defaultValue", "methodIndex"})
    public FlagData(List<String> names, String description, boolean defaultValue, int methodIndex) {
        this.names = names;
        this.description = description;
        this.defaultValue = defaultValue;
        this.methodIndex = methodIndex;
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMethodIndex() {
        return this.methodIndex;
    }
}

