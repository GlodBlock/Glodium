package com.glodblock.github.glodium.reflect.moon;

import com.glodblock.github.glodium.Glodium;
import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@ApiStatus.Internal
public final class Moon {

    private static final Unsafe UNSAFE = unsafe();
    private static final MethodHandles.Lookup LOOKUP = lookup();

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

    public static void removeFinal(Field field) {
        try {
            var m = LOOKUP.findVarHandle(Field.class, "modifiers", int.class);
            var modify = field.getModifiers();
            m.set(field, modify & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Glodium.LOGGER.error(e.getMessage());
        }
    }

    @SuppressWarnings("removal")
    public static void setField(Field field, Object owner, Object value) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            UNSAFE.ensureClassInitialized(field.getDeclaringClass());
            putHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value);
        } else {
            putHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field), value);
        }
    }

    @SuppressWarnings("removal")
    public static Object getField(Field field, Object owner) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            UNSAFE.ensureClassInitialized(field.getDeclaringClass());
            return getHelper(field.getType(), UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field));
        } else {
            return getHelper(field.getType(), owner, UNSAFE.objectFieldOffset(field));
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
