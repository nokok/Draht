package net.nokok.draft;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

public class BindingBuilder {
    private final Class<?> module;

    public BindingBuilder(Class<?> module) {
        if (!module.isInterface()) {
            throw new IllegalArgumentException("DraftModule must be interface");
        }
        this.module = Objects.requireNonNull(module);
    }

    private List<Binding> getBindings(Class<?> module) {
        if (!module.isAnnotationPresent(DraftModule.class)) {
            return Collections.emptyList();
        }
        if (!Modifier.isPublic(module.getModifiers())) {
            System.out.println(String.format("Warnings: Cannot Access Module %s", module));
            return new ArrayList<>();
        }
        List<Binding> bindings = new ArrayList<>(module.getDeclaredMethods().length);
        for (Method method : module.getDeclaredMethods()) {
            Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
            Type bindTo = method.getGenericReturnType();
            Type bindFrom;
            List<Annotation> qualifier;
            int parameterLength = methodGenericParameterTypes.length;
            if (parameterLength == 0) {
                bindFrom = bindTo;
                qualifier = Arrays.asList(method.getAnnotations());
            } else {
                bindFrom = methodGenericParameterTypes[0];
                qualifier = Arrays.asList(method.getParameterAnnotations()[0]);
            }
            if (method.isDefault()) {
                try {
                    ClassLoader classLoader = this.module.getClassLoader();
                    Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{this.module}, (p, m, a) -> {
                        Constructor<MethodHandles.Lookup> ctor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                        ctor.setAccessible(true);
                        return ctor.newInstance(this.module)
                                .in(this.module)
                                .unreflectSpecial(method, this.module)
                                .bindTo(p)
                                .invokeWithArguments(new Object[parameterLength]);
                    });
                    Object result = method.invoke(proxy, new Object[parameterLength]);
                    bindings.add(Binding.withProvider(qualifier, bindFrom, bindTo, new DraftProvider<>(result)));
                } catch (Throwable e) {
                    //invokeWithArguments throws Throwable
                    throw new RuntimeException(e);
                }
            } else {
                bindings.add(new SimpleBinding(qualifier, bindFrom, bindTo));
            }
        }
        return bindings;
    }

    public List<Binding> getBindings() {
        List<Binding> bindings = new ArrayList<>();
        bindings.addAll(getBindings(this.module));
        return bindings;
    }
}
