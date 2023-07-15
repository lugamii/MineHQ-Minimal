package net.lugami.qlib.configuration;

public abstract class AbstractSerializer<T> {

    public abstract String toString(T var1);
    public abstract T fromString(String var1);

}

