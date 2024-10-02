package fr.kinjer.kjda;

import fr.kinjer.kjda.command.KCommandInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import java.util.List;

public abstract class KJDA {

    /**
     * Get the JDA instance
     *
     * @see JDA
     * @return The JDA instance
     */
    public abstract JDA getJDA();

    /**
     * Get the commands with the {@link KCommandInfo} annotation
     *
     * @see KCommandInfo
     * @return The commands
     */
    public abstract List<Object> getCommands();

    /**
     * Add commands to the bot with the {@link KCommandInfo} annotation (required) <br>
     * For the execution of the commands, you must add a method named "run" and return void. <br>
     * The method can invoke parameters of the following types: <br>
     * - {@link SlashCommandInteractionEvent} (if it's a slash command) <br>
     * - {@link MessageContextInteractionEvent} (if it's a message command) <br>
     * - {@link UserContextInteractionEvent} (if it's a user command) <br>
     * - {@link KJDA} to get the instance of KJDA <br>
     * - {@link JDA} to get the instance of JDA <br>
     * - Your main class (to precise for the instance of the KJDA implementation) to get
     *   the instance of your main class <br>
     *
     * Example: <pre>{@code
     * @KCommandInfo(
     *       name = "help",
     *       description = "Display the help message."
     * )
     * public class HelpCommand {
     *    @KCommandExecutor
     *    public void executor(SlashCommandInteractionEvent event, KJDA kjda) {
     *      // Your code
     *    }
     * }
     * }</pre>
     *
     * @param commands The commands
     * @return The KJDA instance
     * @see KCommandInfo
     * @see KJDA#initCommands(String)
     */
    public abstract KJDA addCommands(Object... commands);

    /**
     * Init commands from a package if they are annotated with {@link KCommandInfo}
     *
     * @param packageName The package name
     * @return The KJDA instance
     * @see KCommandInfo
     * @see KJDA#addCommands(Object...)
     */
    public abstract KJDA initCommands(String packageName);

    /**
     * Get the listeners with the {@link fr.kinjer.kjda.listener.KListener} annotation
     *
     * @return The listeners
     */
    public abstract List<Object> getListeners();

    /**
     * Add listeners to the bot with the {@link fr.kinjer.kjda.listener.KListener} annotation (required) <br>
     * Needed to add the {@link fr.kinjer.kjda.listener.KListener} annotation above the class. <br>
     *
     * To treat an event, you must add the {@link fr.kinjer.kjda.listener.KListener} above a method and fill
     * the parameter with the event you want to treat. <br>
     *
     * Example: <pre>{@code
     * @KListener
     * public class YourListener {
     *    @KListener
     *    public void onMessageReceived(MessageReceivedEvent event) {
     *      // Your code
     *    }
     *
     *    @KListener
     *    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
     *      // Your code
     *    }
     * }
     * }</pre>
     *
     * @param listeners The listeners
     * @return The KJDA instance
     */
    public abstract KJDA addListeners(Object... listeners);

    /**
     * Init listeners from a package if they are annotated with {@link fr.kinjer.kjda.listener.KListener}
     *
     * @param packageName The package name
     * @return The KJDA instance
     */
    public abstract KJDA initListeners(String packageName);
}
