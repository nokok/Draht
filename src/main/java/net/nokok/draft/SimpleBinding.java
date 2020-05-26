package net.nokok.draft;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimpleBinding implements Binding {
    private final List<Annotation> annotations;
    private final Type bindFrom;
    private final Type bindTo;

    public SimpleBinding(List<Annotation> annotations, Type bindFrom, Type bindTo) {
        this.annotations = Objects.requireNonNull(annotations);
        this.bindFrom = Objects.requireNonNull(bindFrom);
        this.bindTo = Objects.requireNonNull(bindTo);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public Key getKey() {
        return Key.of(this.annotations, this.bindFrom);
    }

    @Override
    public Type getBindFrom() {
        return bindFrom;
    }

    @Override
    public Type getBindTo() {
        return bindTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBinding binding = (SimpleBinding) o;
        return annotations.equals(binding.annotations) &&
                bindFrom.equals(binding.bindFrom) &&
                bindTo.equals(binding.bindTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotations, bindFrom, bindTo);
    }

    @Override
    public String toString() {
        return String.format("bind[%s:%s] to [%s]", annotations.stream().map(Annotation::toString).collect(Collectors.joining(", ")), bindFrom.getTypeName(), bindTo.getTypeName());
    }
}
