package com.glodblock.github.glodium.reflect;

import java.lang.reflect.Constructor;

public record ConstructorAccessor(Constructor<?> constructor) {

    static final ConstructorAccessor FAIL = new ConstructorAccessor(null);

    public static ConstructorAccessor of(String clazz, Class<?>... paramTypes) {
        try {
            return new ConstructorAccessor(ReflectKit.reflectConstructor(Class.forName(clazz), paramTypes));
        } catch (Throwable e) {
            return ConstructorAccessor.FAIL;
        }
    }

    public static ConstructorAccessor of(Class<?> clazz, Class<?>... paramTypes) {
        try {
            return new ConstructorAccessor(ReflectKit.reflectConstructor(clazz, paramTypes));
        } catch (Throwable e) {
            return ConstructorAccessor.FAIL;
        }
    }

    public boolean valid() {
        return this.constructor != null;
    }

    public <T> T create(Object... args) {
        return ReflectKit.construct(this.constructor, args);
    }

}
