package net.nokok.draft.internal;

import net.nokok.draft.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotatedTypeKey implements Key {
    private final List<? extends Annotation> annotations;
    private final Class<?> clazz;
    private final int hashCode;

    public AnnotatedTypeKey(List<? extends Annotation> annotations, Class<?> clazz) {
        this.annotations = Objects.requireNonNull(annotations);
        this.clazz = Objects.requireNonNull(clazz);
        this.hashCode = Objects.hash(annotations, clazz);
    }

    @Override
    public Class<?> getKeyAsRawType() {
        return this.clazz;
    }

    @Override
    public Type getKeyAsGenericType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isGenericTypeKey() {
        return false;
    }

    @Override
    public List<? extends Annotation> getAnnotations() {
        return this.annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedTypeKey that = (AnnotatedTypeKey) o;
        return annotations.equals(that.annotations) &&
                clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return String.format("Key(%s)", getName());
    }

    @Override
    public String getName() {
        String annotations = this.annotations.stream().map(Annotation::toString).collect(Collectors.joining(","));
        if (annotations.isEmpty()) {
            return clazz.getTypeName();
        } else {
            return String.format("%s:%s", annotations, clazz.getTypeName());
        }
    }
}
