package net.nokok.inject;

import java.lang.reflect.Type;
import java.util.Objects;

public class TypeKey<T> extends Key<T> {
    private final Type type;

    public TypeKey(Class<T> rawType, Type type) {
        super(rawType);
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeKey<?> typeKey = (TypeKey<?>) o;
        return Objects.equals(type, typeKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public String toString() {
        return String.format("TypeKey(%s)", super.getType());
    }
}
