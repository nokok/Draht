package net.nokok.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class Key<T> {
    private final Type type;

    protected Key(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Type getRealType() {
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

    @SuppressWarnings("unchecked")
    public static <T> Key<T> of(Type type) {
        Class<T> rawType;
        if (type instanceof ParameterizedType) {
            return new ParameterizedTypeKey<>((ParameterizedType) type);
        } else {
            rawType = (Class<T>) type;
        }
        return new TypeKey<>(rawType, type);
    }

    public static <T> Key<T> of(Type clazz, Class<? extends Annotation> annotation) {
        return new AnnotatedClassKey<>(clazz, annotation);
    }

    public static <T> Key<T> of(Class<T> clazz, String name) {
        return new StringClassKey<>(clazz, name);
    }
}

