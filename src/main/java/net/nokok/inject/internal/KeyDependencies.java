package net.nokok.inject.internal;

import net.nokok.inject.Key;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        this.dependencies = Arrays.stream(parameters).map(Parameter::getParameterizedType).map(Key::of).collect(Collectors.toList());
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
