package fr.kinjer.kjda.listener;

import fr.kinjer.kjda.KJDABuilder;
import fr.kinjer.kjda.command.KCommandExecutor;
import fr.kinjer.kjda.command.KCommandInfo;
import fr.kinjer.kjda.command.KSubCommand;
import fr.kinjer.kjda.command.option.KOptionData;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KCommandListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(KCommandListener.class);

    private final KJDABuilder kjda;

    public KCommandListener(KJDABuilder kjda) {
        this.kjda = kjda;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        handleCommand(SlashCommandInteractionEvent.class, event);
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        handleCommand(MessageContextInteractionEvent.class, event);
    }

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        handleCommand(MessageContextInteractionEvent.class, event);
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready " + this.kjda.getCommands());
        for (Object command : this.kjda.getCommands()) {
            KCommandInfo commandInfo = command.getClass().getAnnotation(KCommandInfo.class);
            if (commandInfo.type() == Command.Type.SLASH) {
                event.getJDA().getGuilds().forEach(
                        guild -> guild.upsertCommand(commandInfo.name(), commandInfo.description())
                                .addOptions(optionsData(commandInfo.options()))
                                .addSubcommands(subCommandsData(commandInfo.subCommands()))
                                .addSubcommandGroups(Arrays.stream(commandInfo.subCommandGroups())
                                        .map(subCommandGroup -> new SubcommandGroupData(subCommandGroup.name(), subCommandGroup.description())
                                                .addSubcommands(subCommandsData(subCommandGroup.subCommands())))
                                        .toList())
                                .queue((c) -> System.out.println("Commande " + c.getName() + " ajoutée."))
                        );
                continue;
            }
            event.getJDA().getGuilds().forEach(
                    guild -> guild.upsertCommand(new CommandDataImpl(commandInfo.type(), commandInfo.name())).queue());
        }
    }

    private static List<SubcommandData> subCommandsData(KSubCommand[] optionData) {
        return Arrays.stream(optionData).map(KCommandListener::subCommandData).toList();
    }
    private static List<OptionData> optionsData(KOptionData[] optionData) {
        return Arrays.stream(optionData).map(KCommandListener::optionData).toList();
    }
    private static SubcommandData subCommandData(KSubCommand subCommand) {
        return new SubcommandData(subCommand.name(), subCommand.description()).addOptions(optionsData(subCommand.options()));
    }
    private static OptionData optionData(KOptionData optionData) {
        return new OptionData(optionData.type(), optionData.name(), optionData.description(),
                optionData.required(), optionData.isAutoComplete()).addChoices(choicesData(optionData.choices()));
    }
    private static List<Command.Choice> choicesData(KOptionData.Choice[] optionData) {
        return Arrays.stream(optionData).map(KCommandListener::choiceData).toList();
    }
    private static Command.Choice choiceData(KOptionData.Choice choice) {
        return new Command.Choice(choice.name(), choice.value());
    }

    private void handleCommand(Class<?> classEvent, GenericCommandInteractionEvent event) {
        for (Object command : this.kjda.getCommands()) {
            KCommandInfo commandInfo = command.getClass().getAnnotation(KCommandInfo.class);
            if (!commandInfo.name().equals(event.getName())) continue;
            try {
                for (Method declaredMethod : command.getClass().getDeclaredMethods()) {
                    KCommandExecutor commandExecutor = declaredMethod.getAnnotation(KCommandExecutor.class);
                    if (commandExecutor == null) continue;

                    List<Object> params = this.getParams(declaredMethod, classEvent, event);
                    if (classEvent == SlashCommandInteractionEvent.class) {
                        String subCommand = event.getSubcommandName() == null ? "" : event.getSubcommandName();
                        String subCommandGroup = event.getSubcommandGroup() == null ? "" : event.getSubcommandGroup();
                        if (!subCommand.isEmpty() && !subCommandGroup.isEmpty()
                                && commandExecutor.subCommandGroup().equals(subCommand) && commandExecutor.subCommand().equals(subCommandGroup)
                                || !subCommand.isEmpty() && commandExecutor.subCommand().equals(subCommand)
                                || !subCommandGroup.isEmpty() && commandExecutor.subCommandGroup().equals(subCommandGroup)) {
                            declaredMethod.invoke(command, params.toArray());
                            continue;
                        }
                        if (subCommand.isEmpty() && subCommandGroup.isEmpty() && commandExecutor.subCommand().isEmpty() && commandExecutor.subCommandGroup().isEmpty()) {
                            declaredMethod.invoke(command, params.toArray());
                            continue;
                        }
                        continue;
                    }

                    declaredMethod.invoke(command, params.toArray());
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("Error with: " + classEvent.getName() + ", event: " + event.getClass().getName(), e);
                event.reply("Erreur commande, merci de contacter un admin.").setEphemeral(true).queue();
            }
        }
    }

    private List<Object> getParams(Method declaredMethod, Class<?> classEvent, GenericCommandInteractionEvent event) {
        List<Object> params = new ArrayList<>();
        for (Class<?> parameterType : declaredMethod.getParameterTypes()) {
            Object value = this.kjda.getVariable(parameterType);
            if (parameterType == classEvent) {
                params.add(classEvent.cast(event));
            } else if (value != null) {
                params.add(value);
            }
        }
        return params;
    }
}
