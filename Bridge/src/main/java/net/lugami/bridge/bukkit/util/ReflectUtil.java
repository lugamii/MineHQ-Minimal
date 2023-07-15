package net.lugami.bridge.bukkit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {

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