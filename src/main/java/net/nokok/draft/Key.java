package net.nokok.draft;

import net.nokok.draft.internal.AnnotatedTypeKey;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Key {

    abstract public String getName();

    public static Key of(List<? extends Annotation> annotations, Type parameterType) {
        return new AnnotatedTypeKey(annotations, parameterType);
    }

    public static Key of(Type type) {
        if (type instanceof Class<?>) {
            return of((Class<?>) type);
        } else {
            return of((ParameterizedType) type);
        }
    }

    public static Key of(Class<?> clazz) {
        List<Annotation> annotations = Arrays.stream(clazz.getDeclaredAnnotations()).filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class)).collect(Collectors.toList());
        return of(annotations, clazz);
    }

    public static Key of(ParameterizedType p) {
        List<Annotation> annotations = Arrays.stream(((Class<?>) p.getRawType()).getDeclaredAnnotations()).filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class)).collect(Collectors.toList());
        return of(annotations, p);
    }
}
