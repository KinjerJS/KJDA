package fr.kinjer.kjda.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PackageScanner {

    public static <T> Set<T> scanClasses(Class<T> clazz, String path, Instance<Class<?>, T> filter){
        try {
            Set<T> instances = new HashSet<>();
            Set<Class<? extends T>> reflect = new Reflections(path).getSubTypesOf(clazz).stream()
                    .filter(aClass -> aClass.getPackageName().startsWith(path)).collect(Collectors.toSet());
            for (Class<? extends T> aClass : reflect) {
                T instance = filter.initialize(aClass);

                if (instance != null) {
                    instances.add(instance);
                }
            }
            return instances;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> scanAnnotation(Class<? extends Annotation> clazz, String path, Instance<Class<?>, T> filter){
        try {
            List<T> instances = new ArrayList<>();
            Set<Class<?>> reflect = new Reflections(path).getTypesAnnotatedWith(clazz).stream()
                    .filter(aClass -> aClass.getPackageName().startsWith(path)).collect(Collectors.toSet());
            for (Class<?> aClass : reflect) {
                T instance = filter.initialize(aClass);

                if (instance != null) {
                    instances.add(instance);
                }
            }
            return instances;
        } catch (Exception e) {
            return null;
        }
    }

    public interface Instance<T, R> {
        R initialize(T send) throws Exception;
    }

}
