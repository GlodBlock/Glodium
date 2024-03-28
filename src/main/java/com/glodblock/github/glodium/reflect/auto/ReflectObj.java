package com.glodblock.github.glodium.reflect.auto;

public sealed interface ReflectObj permits ConObj, FieldObj, MethodObj {

    <T> T get();

    <T> T get(Object... args);

    void set(Object value);

    ReflectObj with(Object owner);

}
