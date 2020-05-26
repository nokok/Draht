package net.nokok.inject;

import java.lang.reflect.Type;
import java.util.Objects;

public class NamedKey<T> extends Key<T> {
    private final String qualifiedName;

    protected NamedKey(Type keyClass, String qualifiedName) {
        super(keyClass);
        this.qualifiedName = qualifiedName;
        if (this.qualifiedName.isEmpty()) {
            throw new IllegalArgumentException("Empty qualifiedName");
        }
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NamedKey<?> that = (NamedKey<?>) o;
        return Objects.equals(qualifiedName, that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), qualifiedName);
    }
}
