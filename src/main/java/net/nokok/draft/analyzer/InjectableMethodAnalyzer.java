package net.nokok.draft.analyzer;

import net.nokok.draft.InjectableMethod;
import net.nokok.draft.internal.MethodSignature;
import net.nokok.draft.internal.MethodWrapper;
import net.nokok.draft.internal.TypeHierarchy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InjectableMethodAnalyzer {

    private final Class<?> clazz;

    public InjectableMethodAnalyzer(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static InjectableMethodAnalyzer newAnalyzer(Class<?> clazz) {
        return new InjectableMethodAnalyzer(clazz);
    }

    public List<InjectableMethod> runAnalyze() {
        TypeHierarchyAnalyzer typeHierarchyAnalyzer = TypeHierarchyAnalyzer.newAnalyzer(this.clazz);
        TypeHierarchy types = typeHierarchyAnalyzer.runAnalyze();
        Map<Class<?>, List<MethodWrapper>> map = new HashMap<>();

        for (Class<?> c : types.topDownOrder()) {
            List<MethodWrapper> list = new ArrayList<>();
            for (Method m : c.getDeclaredMethods()) {
                MethodWrapper method = MethodWrapper.of(m.getDeclaringClass(), m);
                if (method.isSynthetic() || method.isBridge()) {
                    continue;
                }
                if (method.isStatic()) {
                    continue;
                }
                list.add(method);
            }
            map.put(c, list);
        }

        List<InjectableMethod> methods = new ArrayList<>();
        for (Class<?> clazz : types.topDownOrder()) {
            List<MethodWrapper> methodWrappers = map.get(clazz);
            for (MethodWrapper method : methodWrappers) {
                if (method.hasNoInjectAnnotation()) {
                    continue;
                }
                if (method.isNotOverridable() && method.hasInjectAnnotation()) {
                    methods.add(method.toInjectable());
                    continue;
                }
                MethodSignature methodSignature = method.asSignature();
                Optional<MethodWrapper> overriddenMethodOpt = Optional.empty();
                if (types.hasMoreSubType(clazz)) {
                    for (Class<?> c : types.subTypes(clazz)) {
                        List<MethodWrapper> subTypeMethods = map.getOrDefault(c, Collections.emptyList());
                        Stream<MethodWrapper> methodWrapperStream = subTypeMethods.stream().filter(m -> m.asSignature().equals(methodSignature));
                        if (method.isPackagePrivate()) {
                            methodWrapperStream = methodWrapperStream.filter(m -> m.isSamePackage(method));
                        }

                        List<MethodWrapper> subTypeSimilarMethods = methodWrapperStream.collect(Collectors.toList());
                        if (subTypeSimilarMethods.isEmpty()) {
                            continue;
                        }
                        if (subTypeSimilarMethods.size() != 1) {
                            continue;
                        }
                        overriddenMethodOpt = Optional.of(subTypeSimilarMethods.get(0));
                    }
                }

                if (overriddenMethodOpt.isPresent()) {
                    continue;
                }
                if (method.hasNoInjectAnnotation()) {
                    continue;
                }
                methods.add(method.toInjectable());
            }
        }
        return methods;
    }
}
