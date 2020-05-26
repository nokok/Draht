package net.nokok.inject;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Key<T> {
    private final Type type;

    protected Key(Type type) {
        this.type = type;
    }

    public Type getRawType() {
        if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            return t.getRawType();
        }
        return type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key<?> key = (Key<?>) o;
        return Objects.equals(type, key.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public static boolean hasQualifier(Field field) {
        return Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType().isAnnotationPresent(Qualifier.class));
    }

    public static List<Annotation> getQualifierAnnotations(Field field) {
        return Arrays.stream(field.getAnnotations()).filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class)).collect(Collectors.toList());
    }

    public static <T> Key<T> from(Field field) {
        if (hasQualifier(field)) {
            List<Annotation> qualifierAnnotations = getQualifierAnnotations(field);
        }
        Type fieldType = field.getGenericType();
        return Key.of(fieldType);
    }

    public static <T> Key<T> of(Class<T> clazz) {
        return of((Type) clazz);
    }

    public static <T> Key<T> of(Type type) {
        return new Key<>(type);
    }

    public static <T> Key<T> of(Class<T> type, List<Class<? extends Annotation>> annotations) {
        return of((Type) type, annotations);
    }

    public static <T> Key<T> of(Type type, List<Class<? extends Annotation>> annotations) {
        return new AnnotatedClassKey<>(type, annotations);
    }

    public static <T> Key<T> of(Class<T> type, String name) {
        if (name.isEmpty()) {
            return of((Type) type);
        }
        return of((Type) type, name);
    }

    public static <T> Key<T> of(Type type, String name) {
        if (name.isEmpty()) {
            return of(type);
        }
        return new NamedKey<>(type, name);
    }
}

