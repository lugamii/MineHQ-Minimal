package net.lugami.bridge.bukkit.util;

import java.lang.reflect.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

public class Reflection {

    public static Class<?> getNmsClass(String name) {
        String className = "net.minecraft.server.v1_7_R4." + getVersion() + "." + name;
        return getClass(className);
    }

    public static Class<?> getCbClass(String name) {
        String className = "org.bukkit.craftbukkit.v1_7_R4." + getVersion() + "." + name;
        return getClass(className);
    }

    public static Class<?> getUtilClass(String name) {
        try {
            return Class.forName(name); //Try before 1.8 first
        } catch (ClassNotFoundException ex) {
            try {
                return Class.forName("net.minecraft.util." + name); //Not 1.8
            } catch (ClassNotFoundException ex2) {
                return null;
            }
        }
    }

    public static String getVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static Object getHandle(Object wrapper) {
        Method getHandle = makeMethod(wrapper.getClass(), "getHandle");
        return callMethod(getHandle, wrapper);
    }

    //Utils
    public static Method makeMethod(Class<?> clazz, String methodName, Class<?>... paramaters) {
        try {
            return clazz.getDeclaredMethod(methodName, paramaters);
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T callMethod(Method method, Object instance, Object... paramaters) {
        if (method == null) throw new RuntimeException("No such method");
        method.setAccessible(true);
        try {
            return (T) method.invoke(instance, paramaters);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> makeConstructor(Class<?> clazz, Class<?>... paramaterTypes) {
        try {
            return (Constructor<T>) clazz.getConstructor(paramaterTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T callConstructor(Constructor<T> constructor, Object... paramaters) {
        if (constructor == null) throw new RuntimeException("No such constructor");
        constructor.setAccessible(true);
        try {
            return (T) constructor.newInstance(paramaters);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Field makeField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Field field, Object instance) {
        if (field == null) throw new RuntimeException("No such field");
        field.setAccessible(true);
        try {
            return (T) field.get(instance);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setField(Field field, Object instance, Object value) {
        if (field == null) throw new RuntimeException("No such field");
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static <T> Class<? extends T> getClass(String name, Class<T> superClass) {
        try {
            return Class.forName(name).asSubclass(superClass);
        } catch (ClassCastException | ClassNotFoundException ex) {
            return null;
        }
    }

    // Fuzzy reflection

    public static Field getOnlyField(Class<?> toGetFrom, Class<?> type) {
        Field only = null;
        for (Field field : toGetFrom.getDeclaredFields()) {
            if (!type.isAssignableFrom(field.getClass())) continue;
            Preconditions.checkArgument(only == null, "More than one field of type %s on %s: %s and %s", type.getSimpleName(), toGetFrom.getSimpleName(), field.getName(), only.getName());
            only = field;
        }
        return only;
    }

    public static Method getOnlyMethod(Class<?> toGetFrom, Class<?> returnType, Class<?>... paramSpec) {
        Method only = null;
        for (Method method : toGetFrom.getDeclaredMethods()) {
            if (!returnType.isAssignableFrom(method.getReturnType())) continue;
            if (!isParamsMatchSpec(method.getParameterTypes(), paramSpec)) continue;
            Preconditions.checkArgument(only == null, "More than one method matching spec on %s" + ((only.getName().equals(method.getName())) ? "" : ": " + only.getName() + " " + method.getName()), toGetFrom.getSimpleName());
            only = method;
        }
        return only;
    }

    public static boolean isParamsMatchSpec(Class<?>[] parameters, Class<?>... paramSpec) {
        if (parameters.length > paramSpec.length) return false;
        for (int i = 0; i < paramSpec.length; i++) {
            Class<?> spec = paramSpec[i];
            if (spec == null) continue;
            Class parameter = parameters[i];
            if (!spec.isAssignableFrom(parameter)) return false;
        }
        return true;
    }

    /**
     * Used to invoke a field
     * @param field The field to invoke
     * @param object The object where the field is applicable
     * @exception IllegalArgumentException in case we cannot access the field (Should not happen)
     * @return invoked field
     */
    public static Object invokeField(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a field
     * @param clazz Class where field is applicable
     * @param fieldName Name of the field we're trying to fetch
     * @exception IllegalArgumentException in case the field is not found
     * @return Optional field
     */
    @SuppressWarnings("deprecation")
    public static Field fetchField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (!field.isAccessible())
                field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a constructor
     * @param clazz Class where the constructor is applicable
     * @param parameters Constructor in the constructor we're trying to fetch
     * @exception IllegalArgumentException in case the constructor is not found
     * @return the fetched constructor
     */
    public static Constructor<?> fetchConstructor(Class<?> clazz, Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to invoke a constructor
     * @param constructor The constructor to invoke
     * @param parameters The parameters we need to use to invoke the constructor
     * @exception IllegalArgumentException in case the constructor is not found
     * @return invoked constructor
     */
    public static Object invokeConstructor(Constructor<?> constructor, Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Used to fetch a method
     * @param clazz Class where the method is applicable
     * @param methodName The name of the method we're fetching
     * @param parameters The parameters of the method
     * @return the fetched method
     */
    public static Method fetchMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * Used to invoke a method
     * @param method The method to invoke
     * @param object The object which contains the method we need to invoke
     * @param parameters The parameters needed to invoke the method
     * @return The method's returning object
     */
    public static Object invokeMethod(Method method, Object object, Object... parameters) {
        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Finds a class and calls it back using a {@link BiConsumer}
     * @param name Class' name
     * @param callback callback after class is found
     */
    public static void getClassCallback(String name, BiConsumer<Class<?>, Throwable> callback) {
        CompletableFuture<Class<?>> completableFuture = new CompletableFuture<>();

        completableFuture.complete(getClass(name));
        completableFuture.whenCompleteAsync(callback);
    }

    public static <T> T get(Field field, Object instance) {
        try {
            field.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, modifiers.getInt(field) & ~Modifier.FINAL);

            return (T) field.get(instance);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> holder, Class<?> type, String name) {
        try {
            for (Field field : holder.getDeclaredFields()) {
                if (!field.getName().equalsIgnoreCase(name) || (type != null && field.getType() != type)) continue;

                field.setAccessible(true);
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, modifiers.getInt(field) & ~Modifier.FINAL);

                return field;
            }

            for (Field field : holder.getFields()) {
                if (!field.getName().equalsIgnoreCase(name) || (type != null && field.getType() != type)) continue;

                field.setAccessible(true);
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(field, modifiers.getInt(field) & ~Modifier.FINAL);

                return field;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}