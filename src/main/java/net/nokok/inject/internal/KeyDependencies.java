package net.nokok.inject.internal;

import net.nokok.inject.Key;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyDependencies<T> {
    private final Key<T> key;
    private final List<Key<?>> dependencies;

    private KeyDependencies(Class<T> clazz) {
        this.key = Key.of(clazz);
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new IllegalArgumentException("コンストラクタが見つかりませんでした : " + clazz.getName());
        }
        Constructor<?> constructor;
        if (constructors.length == 1) {
            constructor = constructors[0];
        } else {
            constructor = Arrays.stream(constructors)
                    .filter(c -> c.isAnnotationPresent(Inject.class))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("コンストラクタが複数見つかりましたが、Injectアノテーションが見つかりません"));
        }
        Parameter[] parameters = constructor.getParameters();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        List<Key<?>> keys = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            Annotation[] parameterAnnotations = annotations[i];
            Optional<Named> qualifiedNameOpt = Arrays.stream(annotations[i]).filter(a -> a.annotationType().equals(Named.class)).map(Named.class::cast).findFirst();
            Type type = p.getParameterizedType();
            if (qualifiedNameOpt.isPresent()) {
                Named named = qualifiedNameOpt.get();
                keys.add(Key.of(type, named.value()));
            } else {
                List<Annotation> otherAnnotations = Arrays
                        .stream(parameterAnnotations)
                        .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                        .collect(Collectors.toList());
                if (otherAnnotations.isEmpty()) {
                    keys.add(Key.of(type));
                } else {
                    keys.add(Key.of(type, otherAnnotations.stream().map(Annotation::annotationType).collect(Collectors.toList())));
                }
            }
        }
        this.dependencies = keys;
        if (this.dependencies.size() != parameters.length) {
            throw new IllegalStateException("diff");
        }
    }

    public Key<T> getKey() {
        return key;
    }

    public List<Key<?>> getDependencies() {
        return dependencies;
    }

    public static <T> KeyDependencies<T> find(Class<T> clazz) {
        return new KeyDependencies<>(clazz);
    }
}
