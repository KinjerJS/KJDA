package fr.kinjer.kjda.listener;

import fr.kinjer.kjda.KJDA;
import fr.kinjer.kjda.KJDABuilder;
import fr.kinjer.kjda.command.KCommandExecutor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class KListenerListener extends ListenerAdapter {
    private final KJDABuilder kjda;

    public KListenerListener(KJDABuilder kjda) {
        this.kjda = kjda;
    }

    @Override
    public void onGenericEvent(GenericEvent event) {
        for (Object listener : this.kjda.getListeners()) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                try {
                    if (!method.isAnnotationPresent(KListener.class)) continue;

                    Class<?>[] paramaterTypes = method.getParameterTypes();

                    if (paramaterTypes.length == 0) break;

                    if (paramaterTypes[0].equals(event.getClass())) {
                        List<Object> params = new ArrayList<>();

                        for (Class<?> parameterType : paramaterTypes) {
                            Object value = this.kjda.getVariable(parameterType);
                            if (parameterType == event.getClass()) {
                                params.add(event.getClass().cast(event));
                            } else if (value != null) {
                                params.add(value);
                            }
                        }
                        method.invoke(listener, params.toArray());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }
}
