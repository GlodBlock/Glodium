package com.glodblock.github.glodium.reflect.auto;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectHost {

    boolean silent() default false;

}
