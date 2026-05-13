package com.glodblock.github.glodium.reflect;

import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.reflect.moon.Moon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ReflectKit {

    private ReflectKit() {
        // NO-OP
    }

    @SuppressWarnings("all")
    public static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        Method m = null;
        try {
            m = owner.getDeclaredMethod(name, paramTypes);
        } catch (NoSuchMethodException ignore) {
        }
        if (m == null) throw new NoSuchMethodException("Can't find field from " + name);
        m.setAccessible(true);
        return m;
    }

    @SuppressWarnings("all")
    public static Field reflectField(Class<?> owner, String name) throws NoSuchFieldException {
        Field f = null;
        try {
            f = owner.getDeclaredField(name);
        } catch (NoSuchFieldException ignore) {
        }
        if (f == null) throw new NoSuchFieldException("Can't find field from " + name);
        f.setAccessible(true);
        return f;
    }

    @SuppressWarnings("unchecked")
    public static <T> T readField(Object owner, Field field) {
        try {
            return (T) Moon.getField(field, owner);
        } catch (Exception e) {
            Glodium.LOGGER.error("Reflect error.", e);
            throw new IllegalStateException("Failed to read field: " + field);
        }
    }

    public static void writeField(Object owner, Field field, Object value) {
        try {
            Moon.setField(field, owner, value);
        } catch (Exception e) {
            Glodium.LOGGER.error("Reflect error.", e);
            throw new IllegalStateException("Failed to write field: " + field);
        }
    }

    public static void executeMethod(Object owner, Method method, Object ... args) {
        try {
            method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Glodium.LOGGER.error("Reflect error.", e);
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeMethod2(Object owner, Method method, Object ... args) {
        try {
            return (T) method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Glodium.LOGGER.error("Reflect error.", e);
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

}
