package net.nokok.draft;

import javax.inject.Provider;
import java.util.logging.Logger;

public class LazyDraftProvider implements Provider<Object> {

    private static final Logger logger = Logger.getLogger(LazyDraftProvider.class.getName());
    private final Injector injector;
    private final Key key;

    public LazyDraftProvider(Injector injector, Key key) {
        this.injector = injector;
        this.key = key;
    }

    @Override
    public Object get() {
        logger.info(String.format("%s.get() invoked", this));
        Object instance = this.injector.getInstance(key);
        logger.info("Instance: " + instance);
        return instance;
    }

    @Override
    public String toString() {
        return String.format("LazyDraftProvider(%s)", key);
    }
}
