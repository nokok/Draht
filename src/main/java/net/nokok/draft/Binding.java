package net.nokok.draft;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public interface Binding {
    List<Annotation> getAnnotations();

    Key getKey();

    Type getBindFrom();

    Type getBindTo();

    default boolean hasProvider() {
        return this instanceof ProviderBinding;
    }

    public static Binding of(List<Annotation> annotations, Type bindFrom, Type bindTo) {
        return new SimpleBinding(annotations, bindFrom, bindTo);
    }

    public static Binding withProvider(List<Annotation> annotations, Type bindFrom, Type bindTo, Provider<?> provider) {
        return new ProviderBinding(annotations, bindFrom, bindTo, provider);
    }
}
