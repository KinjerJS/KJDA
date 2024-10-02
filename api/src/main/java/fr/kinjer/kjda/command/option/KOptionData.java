package fr.kinjer.kjda.command.option;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public @interface KOptionData {

    OptionType type();

    String name();

    String description();

    boolean required() default false;

    boolean isAutoComplete() default false;

    Choice[] choices() default {};

    @interface Choice {

        String name();

        String value();

    }
}
