package net.lugami.qlib.command.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EasyMethod {
    private final EasyClass<?> owner;
    private Method method;
    private final Object[] parameters;

    public EasyMethod(EasyClass<?> owner, String name, Object ... parameters) {
        this.owner = owner;
        this.parameters = parameters;
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            classes[i] = parameters[i].getClass();
        }
        try {
            this.method = owner.getClazz().getDeclaredMethod(name, classes);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Object invoke() {
        this.method.setAccessible(true);
        if (this.method.getReturnType().equals(Void.TYPE)) {
            try {
                this.method.invoke(this.owner.get(), this.parameters);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            return this.method.getReturnType().cast(this.method.invoke(this.owner.get(), this.parameters));
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}

