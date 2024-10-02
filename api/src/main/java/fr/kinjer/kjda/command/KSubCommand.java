package fr.kinjer.kjda.command;

import fr.kinjer.kjda.command.option.KOptionData;

public @interface KSubCommand {

    String name();

    String description();

    KOptionData[] options() default {};

}
