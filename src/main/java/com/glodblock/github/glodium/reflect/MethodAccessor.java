package com.glodblock.github.glodium.reflect;

import java.lang.reflect.Method;

public record MethodAccessor(Method method) {

    static final MethodAccessor FAIL = new MethodAccessor(null);

    public static MethodAccessor of(String clazz, String methodName, Class<?>... paramTypes) {
        try {
            return new MethodAccessor(ReflectKit.reflectMethod(Class.forName(clazz), methodName, paramTypes));
        } catch (Throwable e) {
            return MethodAccessor.FAIL;
        }
    }

    public static MethodAccessor of(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return new MethodAccessor(ReflectKit.reflectMethod(clazz, methodName, paramTypes));
        } catch (Throwable e) {
            return MethodAccessor.FAIL;
        }
    }

    public boolean valid() {
        return this.method != null;
    }

    public <T> T get(Object host, Object... args) {
        return ReflectKit.executeMethod2(host, this.method, args);
    }

    public void execute(Object host, Object... args) {
        ReflectKit.executeMethod(host, this.method, args);
    }

    public <T> T getStatic(Object... args) {
        return ReflectKit.executeMethod2(null, this.method, args);
    }

    public void executeStatic(Object... args) {
        ReflectKit.executeMethod(null, this.method, args);
    }

}
