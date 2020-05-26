package net.nokok.draft;

import javax.inject.Provider;
import java.util.Objects;

public class DraftProvider<T> implements Provider<T> {

    private final T instance;

    public DraftProvider(T instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DraftProvider<?> that = (DraftProvider<?>) o;
        return Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance);
    }

    @Override
    public T get() {
        return this.instance;
    }
}
