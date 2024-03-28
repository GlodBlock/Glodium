package com.glodblock.github.glodium.reflect.auto;

import com.glodblock.github.glodium.reflect.ReflectKit;

import java.lang.reflect.Constructor;

public final class ConObj implements ReflectObj {

    private final Constructor<?> con;

    ConObj(Constructor<?> con) {
        this.con = con;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get() {
        return (T) ReflectKit.construct(this.con);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object... args) {
        return (T) ReflectKit.construct(this.con, args);
    }

    @Override
    public void set(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConObj with(Object owner) {
        throw new UnsupportedOperationException();
    }

}
