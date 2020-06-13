package net.nokok.draft.internal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodSignature {
    private final String name;
    private final List<Class<?>> parameters;

    public MethodSignature(String name, List<Class<?>> parameters) {
        this.name = Objects.requireNonNull(name);
        this.parameters = Objects.requireNonNull(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodSignature methodSignature = (MethodSignature) o;
        return name.equals(methodSignature.name) &&
                parameters.equals(methodSignature.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, parameters.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
    }
}
