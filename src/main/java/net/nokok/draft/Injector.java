package net.nokok.draft;

import net.nokok.draft.analyzer.DependencyAnalyzer;
import net.nokok.draft.analyzer.InjectableMethodAnalyzer;
import net.nokok.draft.analyzer.TypeHierarchyAnalyzer;
import net.nokok.draft.internal.TypeHierarchy;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Injector {

    private final Logger logger = Logger.getLogger(Injector.class.getName());
    private final Map<Key, Dependencies> dependencies;
    private final Map<Key, Object> singletonInstances;
    private final Map<Method, Void> injected = new HashMap<>();


    private Injector(Map<Key, Dependencies> dependencies) {
        this.dependencies = Objects.requireNonNull(dependencies);
        this.singletonInstances = new HashMap<>();
    }

    public static Injector newInstance() {
        return new Injector(new HashMap<>());
    }

    public static Injector fromModule(Class<?> module) {
        if (!module.isAnnotationPresent(Module.class)) {
            throw new IllegalArgumentException("@Module annotation not found");
        }
        Map<Key, Dependencies> dependenciesMap = analyzeDependencies(module);
        return new Injector(dependenciesMap);
    }

    private static Map<Key, Dependencies> analyzeDependencies(Class<?> module) {
        Map<Key, Dependencies> dependencyMappings = new HashMap<>();
        Map<Key, DependencyAnalyzer> analyzerCache = new HashMap<>();
        BindingBuilder bindingBuilder = new BindingBuilder(module);
        List<Binding> bindings = bindingBuilder.getBindings();
        for (Binding binding : bindings) {
            Key key = binding.getKey();
            Type bindTo = binding.getBindTo();
            if (dependencyMappings.containsKey(key)) {
                throw new IllegalStateException("Duplicate Entry :" + binding);
            }
            analyzerCache.computeIfAbsent(key, i -> DependencyAnalyzer.newAnalyzer(bindTo));
            DependencyAnalyzer analyzer = analyzerCache.get(key);
            dependencyMappings.put(key, analyzer.runAnalyze());
        }
        return dependencyMappings;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        return (T) getInstance((Type) clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Type type) {
        return (T) getInstance(Key.of(type));
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(List<? extends Annotation> annotations, Type type) {
        Key key = Key.of(annotations, type);
        // logger.info("Resolving... " + key);
        if (key.isProviderTypeKey()) {
            Provider<?> providerInstance = getProviderInstance(key);
            // logger.info(String.format("getInstance(List<Annotation>, Type) -> %s", providerInstance));
            return (T) providerInstance;
        }
        try {
            if (this.singletonInstances.containsKey(key)) {
                // logger.info("Singleton instance found: " + key);
                Object singleton = this.singletonInstances.get(key);
                // logger.info(String.format("getInstance(List<Annotation>, Type) -> %s", singleton));
                return (T) singleton;
            }
            Dependencies dependencies = getDependencies(key);
            Object i = newInstanceWithConstructorInjection(dependencies);
            List<Class<?>> typeHierarchy = buildTypeHierarchy(i);
            InjectableMethodAnalyzer injectableMethodAnalyzer = InjectableMethodAnalyzer.newAnalyzer(i.getClass());
            List<InjectableMethod> injectableMethods = injectableMethodAnalyzer.runAnalyze();
            for (Class<?> clazz : typeHierarchy) {
                injectFields(clazz, i);
                injectMethods(i, injectableMethods.stream().filter(m -> m.getDeclaredClass().equals(clazz)).collect(Collectors.toList()));
            }
            if (key.isSingletonRequired() && dependencies.getTargetConstructor().getDeclaringClass().isAnnotationPresent(Singleton.class)) {
                this.singletonInstances.put(key, i);
            }
//            // logger.info(String.format("getInstance(List<Annotation>, Type) -> %s", finalInstance));
            return (T) i;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void injectMethods(Object i, List<InjectableMethod> injectableMethods) throws ReflectiveOperationException {
        for (InjectableMethod method : injectableMethods) {
            Object[] objects = method.getParameterTypeKeys().stream().map(key -> {
                if (key.isProviderTypeKey()) {
                    // To avoid type inference error
                    return (Object) getProviderInstance(key);
                } else {
                    return (Object) getInstance(key);
                }
            }).toArray();
            method.invoke(i, objects);
        }
    }

    private Provider<?> getProviderInstance(Key key) {
        if (!key.isProviderTypeKey()) {
            throw new IllegalArgumentException();
        }
        Type type = key.getKeyAsProviderType().orElseThrow(IllegalArgumentException::new);
        Provider<?> instance = new LazyDraftProvider(this, Key.of(key.getAnnotations(), type));
        return instance;
    }

    private Dependencies getDependencies(Key key) {
        Dependencies dependencies;
        if (this.dependencies.containsKey(key)) {
            dependencies = this.dependencies.get(key);
        } else {
            DependencyAnalyzer analyzer = DependencyAnalyzer.newAnalyzer(key.getKeyAsRawType());
            dependencies = analyzer.runAnalyze();
            this.dependencies.put(key, dependencies);
        }
        return dependencies;
    }

    private Object newInstanceWithConstructorInjection(Dependencies dependencies) throws ReflectiveOperationException {
        Constructor<?> targetConstructor = dependencies.getTargetConstructor();
        targetConstructor.setAccessible(true);
        List<Object> args = new ArrayList<>(targetConstructor.getParameterCount());
        for (Key dep : dependencies.getDependencyKeys()) {
            args.add(getInstance(dep));
        }
        return targetConstructor.newInstance(args.toArray());
    }

    private List<Class<?>> buildTypeHierarchy(Object obj) {
        return TypeHierarchyAnalyzer.newAnalyzer(obj.getClass()).runAnalyze().topDownOrder();
    }

    private void injectFields(Class<?> clazz, Object obj) throws ReflectiveOperationException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getDeclaredAnnotation(Inject.class) == null) {
                continue;
            }
            Annotation[] annotations = field.getAnnotations();
            field.setAccessible(true);
            Key fieldKey = Key.of(Arrays.asList(annotations), field.getGenericType());
            Object instance = getInstance(fieldKey);
            // logger.info(String.format("%s#%s = %s", clazz, field, instance));
            field.set(obj, instance);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Key key) {
        Object instance = getInstance(key.getAnnotations(), key.isGenericTypeKey() ? key.getKeyAsGenericType() : key.getKeyAsRawType());
        // logger.info(String.format("getInstance(%s) -> %s", key, instance));
        return (T) instance;
    }
}
