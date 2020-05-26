package net.nokok.draft;

import java.util.List;
import java.util.stream.Collectors;

public class Dependencies {
    private final List<Key> dependencies;

    public Dependencies(List<Key> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean isEmpty() {
        return this.dependencies.isEmpty();
    }

    public int dependencySize() {
        return this.dependencies.size();
    }

    public List<Key> getKeys() {
        return this.dependencies;
    }

    @Override
    public String toString() {
        return String.format("Dependencies(%s)", dependencies.stream().map(Key::getName).collect(Collectors.joining(", ")));
    }
}
