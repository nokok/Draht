package net.nokok.draft.internal;

import net.nokok.draft.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotatedTypeKey extends Key {
    private final List<? extends Annotation> annotations;
    private final Type type;
    private final int hashCode;

    public AnnotatedTypeKey(List<? extends Annotation> annotations, Type type) {
        this.annotations = Objects.requireNonNull(annotations);
        this.type = Objects.requireNonNull(type);
        this.hashCode = Objects.hash(annotations, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedTypeKey that = (AnnotatedTypeKey) o;
        return annotations.equals(that.annotations) &&
                type.equals(that.type);
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
            return type.getTypeName();
        } else {
            return String.format("%s:%s", annotations, type.getTypeName());
        }
    }
}
