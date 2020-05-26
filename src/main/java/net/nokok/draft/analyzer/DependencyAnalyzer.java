package net.nokok.draft.analyzer;

import net.nokok.draft.Dependencies;

import java.lang.reflect.Type;

public interface DependencyAnalyzer {

    static DependencyAnalyzer newAnalyzer(Type type) {
        return new ConstructorDependencyAnalyzer(type);
    }

    Dependencies runAnalyze();
}
