package fr.kinjer.kjda.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KCommandExecutor {

    String subCommand() default "";

    String subCommandGroup() default "";

}
