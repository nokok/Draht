package net.nokok.draft.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeHierarchy {
    private final List<Class<?>> topdownHierarchy;
    private final List<Class<?>> bottomUpHierarchy;

    public TypeHierarchy(List<Class<?>> classes) {
        if (classes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (classes.size() == 1) {
            this.topdownHierarchy = new ArrayList<>(classes);
            this.bottomUpHierarchy = new ArrayList<>(classes);
        } else {
            List<Class<?>> c = new ArrayList<>(classes);
            c.sort((left, right) -> {
                if (left.equals(right)) {
                    return 0;
                }
                return left.isAssignableFrom(right) ? -1 : 1;
            });
            this.topdownHierarchy = c;
            this.bottomUpHierarchy = new ArrayList<>(c);
            Collections.reverse(this.bottomUpHierarchy);
        }
    }

    public List<Class<?>> topDownOrder() {
        return new ArrayList<>(this.topdownHierarchy);
    }

    public List<Class<?>> bottomUpOrder() {
        return new ArrayList<>(this.bottomUpHierarchy);
    }

    public boolean hasMoreSubType(Class<?> clazz) {
        return !subTypes(clazz).isEmpty();
    }

    public List<Class<?>> subTypes(Class<?> clazz) {
        if (!this.topdownHierarchy.contains(clazz)) {
            throw new IllegalArgumentException();
        }
        return new ArrayList<>(this.topdownHierarchy).subList(this.topdownHierarchy.indexOf(clazz) + 1, this.topdownHierarchy.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeHierarchy that = (TypeHierarchy) o;
        return topdownHierarchy.equals(that.topdownHierarchy) &&
                bottomUpHierarchy.equals(that.bottomUpHierarchy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topdownHierarchy, bottomUpHierarchy);
    }

    @Override
    public String toString() {
        return this.topdownHierarchy.stream().map(Class::getSimpleName).collect(Collectors.joining(" -> "));
    }
}
