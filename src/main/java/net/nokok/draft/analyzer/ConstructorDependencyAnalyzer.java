package net.nokok.draft.analyzer;

import net.nokok.draft.Dependencies;
import net.nokok.draft.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConstructorDependencyAnalyzer implements DependencyAnalyzer {

    private final Type type;

    ConstructorDependencyAnalyzer(Type type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public Dependencies runAnalyze() {
        Class<?> rawType;
        if (type instanceof Class<?>) {
            rawType = (Class<?>) type;
        } else {
            rawType = (Class<?>) ((ParameterizedType) type).getRawType();
        }

        ConstructorPicker picker = new ConstructorPicker(rawType);
        Constructor<?> constructor = picker.getConstructor();

        Type[] genericParameterTypes = constructor.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        List<Key> keys = new ArrayList<>(genericParameterTypes.length);
        for (int i = 0; i < genericParameterTypes.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Type genericParameterType = genericParameterTypes[i];
            keys.add(Key.of(List.of(parameterAnnotation), genericParameterType));
        }
        return new Dependencies(rawType, keys);
    }
}
