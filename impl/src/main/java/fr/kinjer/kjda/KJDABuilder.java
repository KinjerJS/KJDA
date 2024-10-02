package fr.kinjer.kjda;

import fr.kinjer.kjda.command.KCommandInfo;
import fr.kinjer.kjda.listener.KCommandListener;
import fr.kinjer.kjda.listener.KListener;
import fr.kinjer.kjda.listener.KListenerListener;
import fr.kinjer.kjda.utils.PackageScanner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.*;

public class KJDABuilder extends KJDA {

    private Map<Class<?>, Object> variables = new HashMap<>();

    private final List<Object> commands = new ArrayList<>();
    private final List<Object> listeners = new ArrayList<>();

    private final JDA jda;

    private KJDABuilder(JDABuilder builder) {
        builder.addEventListeners(new KCommandListener(this));
        builder.addEventListeners(new KListenerListener(this));
        this.jda = builder.build();
        this.initVariables();
    }

    private void initVariables() {
        this.addVariable(this);
        this.addVariable(this.getJDA());
    }

    public KJDABuilder addVariable(Object object) {
        this.variables.put(object.getClass(), object);
        return this;
    }

    @Override
    public JDA getJDA() {
        return this.jda;
    }

    @Override
    public KJDA addCommands(Object... commands) {
        for (Object command : commands) {
            KCommandInfo info = command.getClass().getAnnotation(KCommandInfo.class);
            if (info != null) {
                this.commands.add(command);
            }
        }
        return this;
    }

    @Override
    public KJDA initCommands(String packageName) {
        this.commands.addAll(PackageScanner.scanAnnotation(KCommandInfo.class, packageName,
                c -> c.getConstructor().newInstance()));
        return this;
    }

    @Override
    public KJDA addListeners(Object... listeners) {
        for (Object command : listeners) {
            KListener listener = command.getClass().getAnnotation(KListener.class);
            if (listener != null) {
                this.listeners.add(command);
            }
        }
        return this;
    }

    @Override
    public KJDA initListeners(String packageName) {
        this.listeners.addAll(PackageScanner.scanAnnotation(KListener.class, packageName,
                c -> c.getConstructor().newInstance()));
        return this;
    }

    @Override
    public List<Object> getCommands() {
        return commands;
    }

    @Override
    public List<Object> getListeners() {
        return listeners;
    }

    public static KJDABuilder create(JDABuilder builder) {
        return new KJDABuilder(builder);
    }

    public static KJDABuilder create(String token) {
        return new KJDABuilder(JDABuilder.createDefault(token));
    }

    public static KJDABuilder createAll(String token) {
        return create(JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values()))
                .setStatus(OnlineStatus.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.NONE)
                .enableCache(Arrays.asList(CacheFlag.values())));
    }

    public KJDA build() {
        return this;
    }

    public Map<Class<?>, Object> getVariables() {
        return this.variables;
    }

    public Object getVariable(Class<?> parameterType) {
        return this.variables.get(parameterType);
    }
}
