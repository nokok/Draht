package net.nokok.draft;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Dependencies {
    private final Type instantiateTarget;
    private final List<Key> dependencies;

    public Dependencies(Type instantiateTarget, List<Key> dependencies) {
        this.instantiateTarget = Objects.requireNonNull(instantiateTarget);
        this.dependencies = Objects.requireNonNull(dependencies);
    }

    public boolean isEmpty() {
        return this.dependencies.isEmpty();
    }

    public Constructor<?> getTargetConstructor() {
        Class<?> target;
        if (instantiateTarget instanceof Class<?>) {
            target = (Class<?>) this.instantiateTarget;
        } else {
            ParameterizedTypeImpl p = (ParameterizedTypeImpl) instantiateTarget;
            target = (Class<?>) p.getRawType();
        }
        Class<?>[] args = dependencies.stream().map(Key::getKeyAsRawType).collect(Collectors.toList()).toArray(new Class[this.dependencies.size()]);
        try {
            return target.getDeclaredConstructor(args);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot find constructor in " + this.instantiateTarget);
        }
    }

    public List<Key> getDependencyKeys() {
        return this.dependencies;
    }

    @Override
    public String toString() {
        return String.format("%s depends on (%s)", this.instantiateTarget, dependencies.stream().map(Key::getName).collect(Collectors.joining(", ")));
    }
}
