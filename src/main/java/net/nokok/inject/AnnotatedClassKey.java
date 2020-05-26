package net.nokok.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class AnnotatedClassKey<T> extends Key<T> {
    private final List<Class<? extends Annotation>> annotations;

    protected AnnotatedClassKey(Type keyClass, List<Class<? extends Annotation>> annotations) {
        super(keyClass);
        this.annotations = annotations;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnnotatedClassKey<?> that = (AnnotatedClassKey<?>) o;
        return Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), annotations);
    }
}
