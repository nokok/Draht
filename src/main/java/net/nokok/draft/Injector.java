package net.nokok.draft;

import net.nokok.draft.analyzer.DependencyAnalyzer;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

public class Injector {

    private final Map<Key, Provider<?>> bindings = new HashMap<>();

    public static Injector fromModules(Class<?>... moduleArray) {
        List<Class<?>> modules = new ArrayList<>(Arrays.asList(moduleArray));

        Map<Key, Dependencies> dependencyMappings = new HashMap<>();
        Map<Key, DependencyAnalyzer> analyzerCache = new HashMap<>();
        for (Class<?> module : modules) {
            BindingBuilder bindingBuilder = new BindingBuilder(module);
            List<Binding> bindings = bindingBuilder.getBindings();

            for (Binding binding : bindings) {
                Key key = binding.getKey();
                Type bindTo = binding.getBindTo();
                if (dependencyMappings.containsKey(key)) {
                    throw new IllegalStateException("Duplicate Entry :" + binding);
                }
                analyzerCache.putIfAbsent(key, DependencyAnalyzer.newAnalyzer(bindTo));
                DependencyAnalyzer analyzer = analyzerCache.get(key);
                dependencyMappings.put(key, analyzer.runAnalyze());
            }
        }
        System.out.println(dependencyMappings);
        return new Injector();
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        return (T) getInstance((Type) clazz);
    }

    private Object getInstance(Type type) {
        return getInstance(Collections.emptyList(), type);
    }

    private Object getInstance(List<? extends Annotation> annotations, Type type) {
        Key key = Key.of(annotations, type);
        return bindings.get(key).get();
    }
}
