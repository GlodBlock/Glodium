package com.glodblock.github.glodium.reflect.auto;

import com.glodblock.github.glodium.reflect.ReflectKit;

import java.lang.reflect.Method;

public final class MethodObj implements ReflectObj {

    private Object owner = null;
    private final Method method;

    MethodObj(Method obj) {
        this.method = obj;
    }

    private MethodObj(Method obj, Object owner) {
        this.method = obj;
        this.owner = owner;
    }

    @Override
    public <T> T get() {
        return ReflectKit.executeMethod2(this.owner, this.method);
    }

    @Override
    public <T> T get(Object... args) {
        return ReflectKit.executeMethod2(this.owner, this.method, args);
    }

    @Override
    public void set(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReflectObj with(Object owner) {
        return new MethodObj(this.method, owner);
    }

}
