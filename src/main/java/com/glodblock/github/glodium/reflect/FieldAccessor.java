package com.glodblock.github.glodium.reflect;

import java.lang.reflect.Field;

public record FieldAccessor(Field field) {

    static final FieldAccessor FAIL = new FieldAccessor(null);

    public static FieldAccessor of(String clazz, String fieldName) {
        try {
            return new FieldAccessor(ReflectKit.reflectField(Class.forName(clazz), fieldName));
        } catch (Throwable e) {
            return FieldAccessor.FAIL;
        }
    }

    public static FieldAccessor of(Class<?> clazz, String fieldName) {
        try {
            return new FieldAccessor(ReflectKit.reflectField(clazz, fieldName));
        } catch (Throwable e) {
            return FieldAccessor.FAIL;
        }
    }

    public boolean valid() {
        return this.field != null;
    }

    public <T> T get(Object host) {
        return ReflectKit.readField(host, this.field);
    }

    public void set(Object host, Object value) {
        ReflectKit.writeField(host, this.field, value);
    }

    public <T> T getStatic() {
        return ReflectKit.readField(null, this.field);
    }

    public void setStatic(Object value) {
        ReflectKit.writeField(null, this.field, value);
    }

}
