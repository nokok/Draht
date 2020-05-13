package net.nokok.inject;

import net.nokok.inject.internal.KeyDependencies;
import net.nokok.inject.internal.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

public class BindingBuilder {
    public static class KeyBuilder<T> {

        private final Class<T> keyClass;

        public KeyBuilder(Class<T> keyClass) {
            this.keyClass = Objects.requireNonNull(keyClass);
        }

        public <Impl extends T> Binding<T, Impl> to(Class<Impl> clazz) {
            Key<T> key = Key.of(keyClass);
            KeyDependencies<Impl> keyDependencies = KeyDependencies.find(clazz);
            return new Binding<>(key, keyDependencies);
        }

        public GenericTypeKeyBuilder<T> withGenericType(Type... types) {
            return new GenericTypeKeyBuilder<>(keyClass, types);
        }

        public AnnotatedKeyBuilder<T> qualifiedWith(Class<? extends Annotation> annotation) {
            return new AnnotatedKeyBuilder<>(keyClass, annotation);
        }

        public StringKeyBuilder<T> qualifiedWith(String name) {
            return new StringKeyBuilder<>(keyClass, name);
        }

    }

    public static class AnnotatedKeyBuilder<Type> {
        private final Class<Type> keyClass;
        private final Class<? extends Annotation> annotation;

        public AnnotatedKeyBuilder(Class<Type> keyClass, Class<? extends Annotation> annotation) {
            this.keyClass = keyClass;
            this.annotation = annotation;
        }

        public <Impl extends Type> Binding<Type, Impl> to(Class<Impl> clazz) {
            Key<Type> key = Key.of(keyClass, annotation);
            KeyDependencies<Impl> keyDependencies = KeyDependencies.find(clazz);
            return new Binding<>(key, keyDependencies);
        }
    }

    public static class StringKeyBuilder<Type> {
        private final Class<Type> keyClass;
        private final String name;

        public StringKeyBuilder(Class<Type> keyClass, String name) {
            this.keyClass = keyClass;
            this.name = name;
        }

        public <Impl extends Type> Binding<Type, Impl> to(Class<Impl> clazz) {
            Key<Type> key = Key.of(keyClass, name);
            KeyDependencies<Impl> keyDependencies = KeyDependencies.find(clazz);
            return new Binding<>(key, keyDependencies);
        }
    }

    public static class GenericTypeKeyBuilder<TT> {
        private final Class<?> rawType;
        private final Type[] types;

        public GenericTypeKeyBuilder(Class<?> rawType, Type[] types) {
            this.rawType = rawType;
            this.types = types;
        }

        public <Impl extends TT> Binding<Type, Impl> to(Class<Impl> clazz) {
            Key<Type> key = Key.of(new ParameterizedTypeImpl(rawType, types));
            KeyDependencies<Impl> keyDependencies = KeyDependencies.find(clazz);
            return new Binding<>(key, keyDependencies);
        }
    }
}
