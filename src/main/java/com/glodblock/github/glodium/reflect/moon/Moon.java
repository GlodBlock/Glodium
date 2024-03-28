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
    public static final Moon MOON = new Moon();

    private Moon() {
        assert MOON == null;
    }

    private static Unsafe unsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
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

    public void removeFinal(Field field) {
        try {
            var m = LOOKUP.findVarHandle(Field.class, "modifiers", int.class);
            var modify = field.getModifiers();
            m.set(field, modify & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Glodium.LOGGER.error(e.getMessage());
        }
    }

}
