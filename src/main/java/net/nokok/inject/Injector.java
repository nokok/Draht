package net.nokok.inject;

import net.nokok.inject.internal.KeyDependencies;

import javax.inject.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Injector {

    private final Map<Key<?>, KeyDependencies<?>> mappings;
    private final Map<Key<?>, Object> instances = new HashMap<>();

    public Injector(Binding<?, ?>... bindings) {
        this.mappings = Arrays.stream(bindings).map(b -> Map.entry(b.getKey(), b.getKeyDependencies())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public <T> T getInstance(Class<T> clazz) {
        return getInstance((Type) clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Type type, String name) {
        System.out.println("Resolving... : " + type.getTypeName());
        Key<T> key = Key.of(type, name);
        if (isProvider(type)) {
            return getProviderInstance(type);
        }
        if (hasNamedAnnotation(type)) {
            System.out.println("Named : " + getQualifiedName(type));
        }
        if (!mappings.containsKey(key)) {
            throw new IllegalArgumentException("Mapping not found");
        }
        try {
            boolean hasSingletonAnnotation = hasSingletonAnnotation(type);
            if (hasSingletonAnnotation && isAlreadyCreatedInstance(type)) {
                return (T) instances.get(Key.of(type));
            }
            Object injected = newInstanceWithConstructorInjection(type);
            Object finalInstance = resolveInjectableMembers(injected.getClass(), injected);
            if (hasSingletonAnnotation) {
                instances.put(Key.of(type), finalInstance);
            }
            return (T) finalInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getInstance(Type type) {
        return getInstance(type, "");
    }

    private Object newInstanceWithConstructorInjection(Type type) throws ReflectiveOperationException {
        Key<?> key = Key.of(type);
        KeyDependencies<?> mapping = mappings.get(key);
        List<Key<?>> dependencies = mapping.getDependencies();
        int ctorParameterLength = dependencies.size();
        List<Type> dependencyList = dependencies.stream().map(Key::getType).collect(Collectors.toList());

        Object[] ctorArgs = new Object[ctorParameterLength];
        for (int i = 0; i < dependencyList.size(); i++) {
            Type dep = dependencyList.get(i);
            if (isProvider(dep)) {
                Provider<?> p = () -> getProviderInstance(dep);
                ctorArgs[i] = p;
            } else {
                ctorArgs[i] = getInstance(dep);
            }
        }

        Class<?>[] ctorParameterTypes = dependencies.stream().map(Key::getRawType).map(c -> (Class<?>) c).collect(Collectors.toList()).toArray(new Class[ctorParameterLength]);

        Type keyType = mapping.getKey().getRawType();
        if (!(keyType instanceof Class<?>)) {
            throw new IllegalArgumentException("Cannot cast Class<?>");
        }
        Class<?> keyClass = (Class<?>) keyType;
        Constructor<?> ctor = keyClass.getDeclaredConstructor(ctorParameterTypes);
        ctor.setAccessible(true);
        return ctor.newInstance(ctorArgs);
    }

    private Object resolveInjectableMembers(Class<?> baseClass, Object obj) throws ReflectiveOperationException {
        if (baseClass.equals(Object.class)) {
            return obj;
        }
        if (baseClass.getSuperclass() != null) {
            resolveInjectableMembers(baseClass.getSuperclass(), obj);
        }
        Field[] fields = baseClass.getDeclaredFields();
        List<Field> injectingField = Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        for (Field field : injectingField) {
            field.setAccessible(true);
            Type fieldType = field.getGenericType();
            List<Annotation> qualifiers = Arrays.stream(field.getAnnotations()).filter(a -> a.annotationType().isAssignableFrom(Qualifier.class)).collect(Collectors.toList());


            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                annotation.annotationType().isAssignableFrom(Qualifier.class);
            }

            if (isProvider(fieldType)) {
                Provider<?> provider = () -> getProviderInstance(fieldType);
                field.set(obj, provider);
            } else {
                field.set(obj, getInstance(fieldType));
            }
        }
        Method[] methods = baseClass.getDeclaredMethods();
        List<Method> injectingMethods = Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        for (Method method : injectingMethods) {
            method.setAccessible(true);
            Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
            Object[] methodArgs = new Object[methodGenericParameterTypes.length];
            for (int i = 0; i < methodGenericParameterTypes.length; i++) {
                Type methodGenericParameterType = methodGenericParameterTypes[i];
                if (isProvider(methodGenericParameterType)) {
                    Provider<?> provider = () -> getProviderInstance(methodGenericParameterType);
                    methodArgs[i] = provider;
                } else {
                    methodArgs[i] = getInstance(methodGenericParameterType);
                }
            }
            method.invoke(obj, methodArgs);
        }
        return obj;
    }

    private boolean isProvider(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return p.getRawType().equals(Provider.class);
        }
        return false;
    }

    private boolean hasSingletonAnnotation(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            return clazz.isAnnotationPresent(Singleton.class);
        }
        return false;
    }

    private boolean hasNamedAnnotation(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            return clazz.isAnnotationPresent(Named.class);
        }
        return false;
    }

    private String getQualifiedName(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            Named annotation = clazz.getAnnotation(Named.class);
            return annotation.value();
        }
        throw new IllegalArgumentException("Annotation Not found");
    }

    private boolean isAlreadyCreatedInstance(Type type) {
        return instances.containsKey(Key.of(type));
    }

    private <T> T getProviderInstance(Type t) {
        ParameterizedType p = ((ParameterizedType) t);
        Type rawType = p.getRawType();
        if (!rawType.equals(Provider.class)) {
            throw new IllegalArgumentException("Cannot cast Provider<?> : " + p.getTypeName());
        }
        return getInstance(p.getActualTypeArguments()[0]);
    }

    public static class syntax {
        public static <T> BindingBuilder.KeyBuilder<T> bind(Class<T> i) {
            return new BindingBuilder.KeyBuilder<>(i);
        }

        public static <T> Binding<T, T> register(Class<T> c) {
            return new BindingBuilder.KeyBuilder<>(c).to(c);
        }
    }
}
