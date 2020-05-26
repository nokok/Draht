package net.nokok.draft;

import net.nokok.draft.internal.InterfaceProxyHandler;

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

    public List<Binding> getBindings() {
        List<Binding> bindings = new ArrayList<>(module.getDeclaredMethods().length);
        for (Method method : module.getDeclaredMethods()) {
            Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
            Type bindTo = method.getGenericReturnType();
            Type bindFrom;
            List<Annotation> bindFromAnnotations;
            int parameterLength = methodGenericParameterTypes.length;
            if (parameterLength == 0) {
                bindFrom = bindTo;
                bindFromAnnotations = Collections.emptyList();
            } else {
                bindFrom = methodGenericParameterTypes[0];
                bindFromAnnotations = Arrays.asList(method.getParameterAnnotations()[0]);
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
                    bindings.add(Binding.withProvider(bindFromAnnotations, bindFrom, bindTo, new DraftProvider<>(result)));
                } catch (Throwable e) {
                    //invokeWithArguments throws Throwable
                    throw new RuntimeException(e);
                }
            } else {
                bindings.add(new SimpleBinding(bindFromAnnotations, bindFrom, bindTo));
            }
        }
        return bindings;
    }
}
