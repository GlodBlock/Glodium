package com.glodblock.github.glodium.reflect.auto;

import com.glodblock.github.glodium.reflect.ReflectKit;

import java.lang.reflect.Field;

public final class FieldObj implements ReflectObj {

    private Object owner = null;
    private final Field field;

    FieldObj(Field obj) {
        this.field = obj;
    }

    private FieldObj(Field obj, Object owner) {
        this.field = obj;
        this.owner = owner;
    }

    @Override
    public <T> T get() {
        return ReflectKit.readField(this.owner, this.field);
    }

    @Override
    public <T> T get(Object... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Object value) {
        ReflectKit.writeField(this.owner, this.field, value);
    }

    @Override
    public FieldObj with(Object owner) {
        return new FieldObj(this.field, owner);
    }

}
