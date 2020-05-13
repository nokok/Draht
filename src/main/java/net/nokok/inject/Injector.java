package net.nokok.inject;

import net.nokok.inject.internal.KeyDependencies;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
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
    public <T> T getInstance(Type type) {
        Key<T> key = Key.of(type);
        if (isProvider(type)) {
            return getProviderInstance(type);
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
            Object finalInstance = resolveInjectableMembers(injected);
            if (hasSingletonAnnotation) {
                instances.put(Key.of(type), finalInstance);
            }
            return (T) finalInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object newInstanceWithConstructorInjection(Type type) throws ReflectiveOperationException {
        Key<?> key = Key.of(type);
        KeyDependencies<?> mapping = mappings.get(key);
        List<Key<?>> dependencies = mapping.getDependencies();
        int ctorParameterLength = dependencies.size();
        List<Type> dependencyList = dependencies.stream().map(Key::getRealType).collect(Collectors.toList());

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

        Class<?>[] ctorParameterTypes = dependencies.stream().map(Key::getType).map(c -> (Class<?>) c).collect(Collectors.toList()).toArray(new Class[ctorParameterLength]);

        Type keyType = mapping.getKey().getType();
        if (!(keyType instanceof Class<?>)) {
            throw new IllegalArgumentException("Cannot cast Class<?>");
        }
        Class<?> keyClass = (Class<?>) keyType;
        Constructor<?> ctor;
        if (ctorParameterLength == 0) {
            ctor = keyClass.getDeclaredConstructor();
        } else {
            ctor = keyClass.getDeclaredConstructor(ctorParameterTypes);
        }
        ctor.setAccessible(true);
        if (ctorParameterLength == 0) {
            return ctor.newInstance();
        } else {
            return ctor.newInstance(ctorArgs);
        }
    }

    private Object resolveInjectableMembers(Object obj) throws ReflectiveOperationException {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Field> injectingField = Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        for (Field field : injectingField) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations.length == 1 && annotations[0].annotationType().equals(Inject.class)) {
                field.setAccessible(true);
                Type fieldType = field.getGenericType();
                if (isProvider(fieldType)) {
                    Provider<?> provider = () -> getProviderInstance(fieldType);
                    field.set(obj, provider);
                } else {
                    field.set(obj, getInstance(fieldType));
                }
            }
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        List<Method> injectingMethods = Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
        for (Method method : injectingMethods) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length == 1 && annotations[0].annotationType().equals(Inject.class)) {
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
                if (methodGenericParameterTypes.length == 0) {
                    method.invoke(obj);
                } else {
                    method.invoke(obj, methodArgs);
                }
            }
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
