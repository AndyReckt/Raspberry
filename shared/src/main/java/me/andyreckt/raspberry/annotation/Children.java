package me.andyreckt.raspberry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Children {
    String[] names();
    String permission() default "";
    String description() default "";
    String usage() default "";
    String helpCommand() default "";
    boolean autoHelp() default true;
    boolean async() default false;
    boolean hidden() default false;
}
