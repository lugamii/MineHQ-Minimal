package net.lugami.qlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Command {
    String[] names();

    String permission();

    boolean hidden() default false;

    boolean async() default false;

    String description() default "";

    boolean logToConsole() default true;
}

