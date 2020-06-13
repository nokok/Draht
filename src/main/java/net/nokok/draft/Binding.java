package net.nokok.draft;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Binding {
    List<Annotation> getAnnotations();

    Key getKey();

    Type getBindFrom();

    Type getBindTo();

    default boolean hasProvider() {
        return this instanceof ProviderBinding;
    }

    default boolean hasValue() {
        return this instanceof InstanceBinding;
    }

    default Optional<Object> getValue() {
        if (this.hasValue()) {
            Object value = ((InstanceBinding) this).getRawValue();
            return Optional.ofNullable(value);
        } else {
            return Optional.empty();
        }
    }

    public static Binding of(List<Annotation> annotations, Type bindFrom, Type bindTo) {
        return new SimpleBinding(annotations, bindFrom, bindTo);
    }

    public static Binding withProvider(List<Annotation> annotations, Type bindFrom, Type bindTo, Provider<?> provider) {
        return new ProviderBinding(annotations, bindFrom, bindTo, provider);
    }

    public static Binding withValue(List<Annotation> annotations, Type bindFrom, Type bindTo, Object instance) {
        return new InstanceBinding(annotations, bindFrom, bindTo, instance);
    }

}
