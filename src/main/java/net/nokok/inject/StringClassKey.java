package net.nokok.inject;

import java.lang.reflect.Type;
import java.util.Objects;

public class StringClassKey<T> extends Key<T> {
    private final String qualifiedName;

    protected StringClassKey(Type keyClass, String qualifiedName) {
        super(keyClass);
        this.qualifiedName = qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StringClassKey<?> that = (StringClassKey<?>) o;
        return Objects.equals(qualifiedName, that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), qualifiedName);
    }
}
