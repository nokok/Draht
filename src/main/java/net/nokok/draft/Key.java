package net.nokok.draft;

import net.nokok.draft.internal.AnnotatedParameterizedTypeKey;
import net.nokok.draft.internal.AnnotatedTypeKey;

import javax.inject.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Key {

    String getName();

    Type getKeyAsGenericType();

    boolean isGenericTypeKey();

    default Class<?> getKeyAsRawType() {
        if (this.isGenericTypeKey()) {
            ParameterizedType p = (ParameterizedType) getKeyAsGenericType();
            return (Class<?>) p.getRawType();
        } else {
            return (Class<?>) this.getKeyAsGenericType();
        }
    }

    List<? extends Annotation> getAnnotations();

    default boolean isSingletonRequired() {
        return this.getKeyAsRawType().isAnnotationPresent(Singleton.class);
    }

    default boolean isProviderTypeKey() {
        if (!this.isGenericTypeKey()) {
            return false;
        }
        return this.getKeyAsRawType().equals(Provider.class);
    }

    default Optional<Type> getKeyAsProviderType() {
        if (!this.isProviderTypeKey()) {
            return Optional.empty();
        }
        Type providerType = ((ParameterizedType) this.getKeyAsGenericType()).getActualTypeArguments()[0];
        return Optional.of(providerType);
    }

    public static Key of(List<? extends Annotation> annotations, Type type) {
        List<? extends Annotation> annotationsWithoutInject = annotations.stream().filter(a -> !a.annotationType().equals(Inject.class)).collect(Collectors.toList());
        if (type instanceof Class<?>) {
            return new AnnotatedTypeKey(annotationsWithoutInject, (Class<?>) type);
        } else {
            return new AnnotatedParameterizedTypeKey(annotationsWithoutInject, (ParameterizedType) type);
        }
    }

    public static Key of(Type type) {
        if (type instanceof Class<?>) {
            return of((Class<?>) type);
        } else {
            return of((ParameterizedType) type);
        }
    }

    public static Key of(Class<?> clazz) {
        List<Annotation> annotations = Arrays.stream(clazz.getDeclaredAnnotations()).filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class)).collect(Collectors.toList());
        return of(annotations, clazz);
    }

    public static Key of(ParameterizedType p) {
        List<Annotation> annotations = Arrays.stream(((Class<?>) p.getRawType()).getDeclaredAnnotations()).filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class)).collect(Collectors.toList());
        return of(annotations, p);
    }
}
