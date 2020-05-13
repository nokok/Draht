package net.nokok.inject;

import net.nokok.inject.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

public class AnnotatedClassKey<T> extends Key<T> {
    private final Class<? extends Annotation> annotation;

    protected AnnotatedClassKey(Type keyClass, Class<? extends Annotation> annotation) {
        super(keyClass);
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnnotatedClassKey<?> that = (AnnotatedClassKey<?>) o;
        return Objects.equals(annotation, that.annotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), annotation);
    }
}
