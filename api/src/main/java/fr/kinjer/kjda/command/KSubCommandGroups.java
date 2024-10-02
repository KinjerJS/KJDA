package fr.kinjer.kjda.command;

public @interface KSubCommandGroups {

    String name();

    String description();

    KSubCommand[] subCommands();

}
