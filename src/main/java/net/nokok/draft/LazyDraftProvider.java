package net.nokok.draft;

import javax.inject.Provider;

public class LazyDraftProvider implements Provider<Object> {

    private final Injector injector;
    private final Key key;

    public LazyDraftProvider(Injector injector, Key key) {
        this.injector = injector;
        this.key = key;
    }

    @Override
    public Object get() {
        return this.injector.getInstance(key);
    }

    @Override
    public String toString() {
        return String.format("LazyDraftProvider(%s)", key);
    }
}
