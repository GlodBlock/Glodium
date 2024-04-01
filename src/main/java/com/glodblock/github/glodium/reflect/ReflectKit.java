package com.glodblock.github.glodium.reflect;

import com.glodblock.github.glodium.reflect.moon.Moon;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectKit {

    public static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return reflectMethod(owner, new String[]{name}, paramTypes);
    }

    @SuppressWarnings("all")
    public static Method reflectMethod(Class<?> owner, String[] names, Class<?>... paramTypes) throws NoSuchMethodException {
        Method m = null;
        for (String name : names) {
            try {
                m = owner.getDeclaredMethod(name, paramTypes);
                if (m != null) break;
            }
            catch (NoSuchMethodException ignore) {
            }
        }
        if (m == null) throw new NoSuchMethodException("Can't find field from " + Arrays.toString(names));
        m.setAccessible(true);
        return m;
    }

    @SuppressWarnings("all")
    public static Field reflectField(Class<?> owner, String... names) throws NoSuchFieldException, IllegalAccessException {
        Field f = null;
        for (String name : names) {
            try {
                f = owner.getDeclaredField(name);
                if (f != null) break;
            }
            catch (NoSuchFieldException ignore) {
            }
        }
        if (f == null) throw new NoSuchFieldException("Can't find field from " + Arrays.toString(names));
        f.setAccessible(true);
        return f;
    }

    public static <T> Constructor<T> reflectCon(Class<T> owner, Class<?>... paramTypes) throws NoSuchMethodException {
        var c = owner.getDeclaredConstructor(paramTypes);
        c.setAccessible(true);
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <T> T readField(Object owner, Field field) {
        try {
            return (T) field.get(owner);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + field);
        }
    }

    public static void writeField(Object owner, Field field, Object value) {
        try {
            field.set(owner, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write field: " + field);
        }
    }

    public static void executeMethod(Object owner, Method method, Object... args) {
        try {
            method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeMethod2(Object owner, Method method, Object... args) {
        try {
            return (T) method.invoke(owner, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to execute method: " + method);
        }
    }

    public static <T> T construct(Constructor<T> con, Object... args) {
        try {
            return con.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException("Failed to create new instance: " + con);
        }
    }

    private static void removeFinal(Field field) {
        var modify = field.getModifiers();
        // remove primitive type's final modifier is meaningless
        if (field.getType().isPrimitive() && Modifier.isFinal(modify)) {
            return;
        }
        if (Modifier.isStatic(modify) && Modifier.isFinal(modify)) {
            Moon.removeFinal(field);
        }
    }

}
