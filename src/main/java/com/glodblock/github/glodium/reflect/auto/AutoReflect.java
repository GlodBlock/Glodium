package com.glodblock.github.glodium.reflect.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoReflect {

    /**
     * The class where the field/method/constructor located.
     */
    Class<?> host() default NAC.class;

    /**
     * The class path when it is private or protected.
     */
    String[] path() default {};

    /**
     * The name of the field/method.
     */
    String[] name() default {};

    /**
     * The parameters of the method/constructor.
     */
    Class<?>[] paras() default {};

    /**
     * The object is at which side.
     */
    Environment side() default Environment.ALL;

    // placeholder
    class NAC {

    }

    enum Environment {
        ALL,
        SERVER,
        CLIENT
    }

}
