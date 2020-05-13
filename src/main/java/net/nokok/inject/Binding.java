package net.nokok.inject;


import net.nokok.inject.internal.KeyDependencies;

public class Binding<Bind, Dep> {
    private final Key<Bind> key;
    private final KeyDependencies<Dep> keyDependencies;

    public Binding(Key<Bind> key, KeyDependencies<Dep> keyDependencies) {
        this.key = key;
        this.keyDependencies = keyDependencies;
    }

    public Key<Bind> getKey() {
        return key;
    }

    public KeyDependencies<Dep> getKeyDependencies() {
        return keyDependencies;
    }
}
