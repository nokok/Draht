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

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Type type) {
        System.out.println("1. getInstance : " + type);
        Key<T> key = Key.of(type);
        System.out.println("2. Key: " + key);
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            if (p.getRawType().equals(Provider.class)) {
                Type actualTypeArgument = p.getActualTypeArguments()[0];
                System.out.println("Provider return");
                return getInstance(actualTypeArgument);
            }
        }
        if (!mappings.containsKey(key)) {
            System.out.println("DEBUG: マッピングが見つかりません " + type.getTypeName());
            return null;
        }
        KeyDependencies<?> mapping = mappings.get(key);
        List<Key<?>> dependencies = mapping.getDependencies();
        Type keyType = mapping.getKey().getType();
        int parameterLength = dependencies.size();
        List<Type> dependencyList = dependencies.stream().map(Key::getRealType).collect(Collectors.toList());
        Object[] args = new Object[parameterLength];
        for (int i = 0; i < dependencyList.size(); i++) {
            Type dep = dependencyList.get(i);
            System.out.println("Resolving... : " + dep.getTypeName());
            if (dep instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) dep;
                if (p.getRawType().equals(Provider.class)) {
                    Type providerType = p.getActualTypeArguments()[0];
                    Provider<?> provider = () -> getInstance(providerType);
                    args[i] = provider;
                }
            } else {
                args[i] = getInstance(dep);
            }
        }
        try {
            Constructor<?> constructor;
            if (keyType instanceof Class) {
                List<Type> parameterList = dependencies.stream().map(Key::getType).collect(Collectors.toList());
                if (!parameterList.stream().allMatch(p -> p instanceof Class)) {
                    throw new UnsupportedOperationException("Cannot cast Class<?> :" + parameterList);
                }
                Class<?>[] parameterTypes = parameterList.stream().map(c -> (Class<?>) c).collect(Collectors.toList()).toArray(new Class[parameterLength]);
                Class<T> implClass = (Class<T>) keyType;
                boolean singleton = implClass.isAnnotationPresent(Singleton.class);
                if (parameterLength == 0) {
                    constructor = implClass.getDeclaredConstructor();
                } else {
                    constructor = implClass.getDeclaredConstructor(parameterTypes);
                }
                constructor.setAccessible(true);
                Object obj;
                if (singleton && instances.containsKey(Key.of(implClass))) {
                    return (T) instances.get(Key.of(implClass));
                }
                if (parameterLength == 0) {
                    obj = constructor.newInstance();
                } else {
                    obj = constructor.newInstance(args);
                }
                if (singleton) {
                    instances.put(Key.of(implClass), obj);
                }
                Field[] fields = obj.getClass().getDeclaredFields();
                List<Field> injectingField = Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
                for (Field field : injectingField) {
                    Annotation[] annotations = field.getAnnotations();
                    if (annotations.length == 1 && annotations[0].annotationType().equals(Inject.class)) {
                        field.setAccessible(true);
                        Type fieldType = field.getGenericType();
                        if (fieldType instanceof ParameterizedType) {
                            ParameterizedType p = (ParameterizedType) fieldType;
                            if (p.getRawType().equals(Provider.class)) {
                                Provider<?> provider = () -> getInstance(fieldType);
                                field.set(obj, provider);
                            }
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
                            if (methodGenericParameterType instanceof ParameterizedType) {
                                ParameterizedType p = (ParameterizedType) methodGenericParameterType;
                                if (p.getRawType().equals(Provider.class)) {
                                    Provider<?> provider = () -> getInstance(methodGenericParameterType);
                                    methodArgs[i] = provider;
                                }
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
                return (T) obj;
            } else {
                throw new UnsupportedOperationException("Unsupported KeyType : " + keyType.getClass().getName());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
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
