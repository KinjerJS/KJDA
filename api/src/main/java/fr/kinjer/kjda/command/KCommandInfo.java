package fr.kinjer.kjda.command;

import fr.kinjer.kjda.command.option.KOptionData;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KCommandInfo {

    Command.Type type() default Command.Type.SLASH;

    String name();

    String description();

    KOptionData[] options() default {};

    KSubCommand[] subCommands() default {};

    KSubCommandGroups[] subCommandGroups() default {};

}
