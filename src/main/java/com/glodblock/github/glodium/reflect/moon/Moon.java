package com.glodblock.github.glodium.reflect.moon;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("removal")
@ApiStatus.Internal
public abstract class Moon {

    private static final Unsafe UNSAFE = unsafe();
    private static final MethodHandles.Lookup LOOKUP = lookup();

    private Moon() {
        // NO-OP
    }

    private static Unsafe unsafe() {
        try {
            Field theSafe = Unsafe.class.getDeclaredField("theUnsafe");
            theSafe.setAccessible(true);
            return (Unsafe) theSafe.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandles.Lookup lookup() {
        try {
            var field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            long offset = UNSAFE.staticFieldOffset(field);
            return (MethodHandles.Lookup) UNSAFE.getObject(MethodHandles.Lookup.class, offset);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object owner, Object value) {
        if (Modifier.isStatic(field.getModifiers())) {
            putHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value);
        } else {
            putHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field), value);
        }
    }

    public static Object getField(Field field, Object owner) {
        if (Modifier.isStatic(field.getModifiers())) {
            return getHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field));
        } else {
            return getHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {
            // NO-OP
        }
        try {
            return (T) UNSAFE.allocateInstance(type);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void putHelper(Class<?> clazz, Object owner, long offset, Object value) {
        if (clazz == Integer.TYPE) {
            UNSAFE.putInt(owner, offset, (int) value);
        } else if (clazz == Short.TYPE) {
            UNSAFE.putShort(owner, offset, (short) value);
        } else if (clazz == Byte.TYPE) {
            UNSAFE.putByte(owner, offset, (byte) value);
        } else if (clazz == Long.TYPE) {
            UNSAFE.putLong(owner, offset, (long) value);
        } else if (clazz == Float.TYPE) {
            UNSAFE.putFloat(owner, offset, (float) value);
        } else if (clazz == Double.TYPE) {
            UNSAFE.putDouble(owner, offset, (double) value);
        } else if (clazz == Boolean.TYPE) {
            UNSAFE.putBoolean(owner, offset, (boolean) value);
        } else if (clazz == Character.TYPE) {
            UNSAFE.putChar(owner, offset, (char) value);
        } else {
            UNSAFE.putObject(owner, offset, value);
        }
    }

    private static Object getHelper(Class<?> clazz, Object owner, long offset) {
        if (clazz == Integer.TYPE) {
            return UNSAFE.getInt(owner, offset);
        } else if (clazz == Short.TYPE) {
            return UNSAFE.getShort(owner, offset);
        } else if (clazz == Byte.TYPE) {
            return UNSAFE.getByte(owner, offset);
        } else if (clazz == Long.TYPE) {
            return UNSAFE.getLong(owner, offset);
        } else if (clazz == Float.TYPE) {
            return UNSAFE.getFloat(owner, offset);
        } else if (clazz == Double.TYPE) {
            return UNSAFE.getDouble(owner, offset);
        } else if (clazz == Boolean.TYPE) {
            return UNSAFE.getBoolean(owner, offset);
        } else if (clazz == Character.TYPE) {
            return UNSAFE.getChar(owner, offset);
        } else {
            return UNSAFE.getObject(owner, offset);
        }
    }

}
