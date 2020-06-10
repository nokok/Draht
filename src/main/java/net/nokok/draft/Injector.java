package net.nokok.draft;

import net.nokok.draft.analyzer.DependencyAnalyzer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

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
            Object finalInstance = resolveInjectableMembers(i);
            if (key.isSingletonRequired() && dependencies.getTargetConstructor().getDeclaringClass().isAnnotationPresent(Singleton.class)) {
                this.singletonInstances.put(key, finalInstance);
            }
//            // logger.info(String.format("getInstance(List<Annotation>, Type) -> %s", finalInstance));
            return (T) finalInstance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Provider<?> getProviderInstance(Key key) {
        if (!key.isProviderTypeKey()) {
            throw new IllegalArgumentException();
        }
        Type type = key.getKeyAsProviderType().orElseThrow(IllegalArgumentException::new);
        Provider<?> instance = new LazyDraftProvider(this, Key.of(key.getAnnotations(), type));
//        // logger.info("Provider instantiate : " + instance);
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

    private Object resolveInjectableMembers(Object obj) throws ReflectiveOperationException {
        List<Class<?>> typeHierarchy = new ArrayList<>();
        Class<?> current = obj.getClass();
        do {
            typeHierarchy.add(current);
            current = current.getSuperclass();
        } while (current.getSuperclass() != null);

        Collections.reverse(typeHierarchy);

        for (Class<?> clazz : typeHierarchy) {
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

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (shouldSkipInject(clazz, typeHierarchy, method, obj)) {
                    continue;
                }
                // logger.info(String.format("TargetMethod: %s#%s", clazz.getSimpleName(), method.getName()));
                method.setAccessible(true);
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
                Object[] methodArgs = new Object[methodGenericParameterTypes.length];
                for (int i = 0; i < methodGenericParameterTypes.length; i++) {
                    Type methodGenericParameterType = methodGenericParameterTypes[i];
                    Key methodKey = Key.of(Arrays.asList(parameterAnnotations[i]), methodGenericParameterType);

                    if (methodKey.isProviderTypeKey()) {
                        methodArgs[i] = getProviderInstance(methodKey);
                    } else {
                        methodArgs[i] = getInstance(methodKey);
                    }
                }
                // logger.info(String.format("%s#%s invoke with %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(methodArgs)));
                if (methodGenericParameterTypes.length == 0) {
                    method.invoke(obj);
                } else {
                    method.invoke(obj, methodArgs);
                }
            }
        }

        return obj;
    }

    private boolean shouldSkipInject(Class<?> targetClass, List<Class<?>> typeHierarchy, Method method, Object instance) {
        if (method.getDeclaredAnnotation(Inject.class) == null) {
            logger.info(String.format("%s:%s#%s -> %s", instance, targetClass.getSimpleName(), method.getName(), true));
            return true;
        }
        if (method.isBridge()) {
            logger.info(String.format("%s:%s#%s -> %s", instance, targetClass.getSimpleName(), method.getName(), true));
            return true;
        }
        if (method.isSynthetic()) {
            logger.info(String.format("%s:%s#%s -> %s", instance, targetClass.getSimpleName(), method.getName(), true));
            return true;
        }

        logger.info(String.format("%s:%s#%s -> %s", instance, targetClass.getSimpleName(), method.getName(), false));
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Key key) {
        Object instance = getInstance(key.getAnnotations(), key.isGenericTypeKey() ? key.getKeyAsGenericType() : key.getKeyAsRawType());
        // logger.info(String.format("getInstance(%s) -> %s", key, instance));
        return (T) instance;
    }
}
