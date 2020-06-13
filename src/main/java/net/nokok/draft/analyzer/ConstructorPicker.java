package net.nokok.draft.analyzer;

import javax.inject.Inject;
import java.lang.reflect.Constructor;

public class ConstructorPicker {
    private final Class<?> clazz;

    public ConstructorPicker(Class<?> clazz) {
        if (!ConstructorPicker.hasInjectableConstructor(clazz)) {
            throw new IllegalArgumentException("No injectable constructor :" + clazz);
        }
        this.clazz = clazz;
    }

    public static boolean hasInjectableConstructor(Class<?> clazz) {
        if (clazz.isInterface()) {
            return false;
        }
        if (clazz.getDeclaredConstructors().length == 0) {
            return false;
        }
        return true;
    }

    public Constructor<?> getConstructor() {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new IllegalArgumentException("No Constructor");
        }
        if (constructors.length == 1) {
            return constructors[0];
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return constructor;
            }
        }

        throw new IllegalStateException(String.format("Cannot find constructor in %s", this.clazz.toString()));
    }
}
