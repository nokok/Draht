package net.nokok.draft.analyzer;

import net.nokok.draft.internal.TypeHierarchy;

import java.util.ArrayList;
import java.util.List;

public class TypeHierarchyAnalyzer {

    private final Class<?> clazz;

    private TypeHierarchyAnalyzer(Class<?> clazz) {
        this.clazz = clazz;
    }

    public TypeHierarchy runAnalyze() {
        List<Class<?>> typeHierarchy = new ArrayList<>();
        Class<?> current = clazz;
        do {
            typeHierarchy.add(current);
            current = current.getSuperclass();
        } while (current.getSuperclass() != null);

        return new TypeHierarchy(typeHierarchy);
    }

    public static TypeHierarchyAnalyzer newAnalyzer(Class<?> clazz) {
        return new TypeHierarchyAnalyzer(clazz);
    }
}
