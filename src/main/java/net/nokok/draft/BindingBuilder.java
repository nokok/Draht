package net.nokok.draft;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BindingBuilder {

    private static final Logger logger = Logger.getLogger(BindingBuilder.class.getName());
    private final Class<?> module;

    public BindingBuilder(Class<?> module) {
        if (!module.isInterface()) {
            throw new IllegalArgumentException("Module must be interface");
        }
        this.module = Objects.requireNonNull(module);
    }

    private List<Binding> getBindings(Class<?> module) {
        if (!module.isAnnotationPresent(Module.class)) {
            return Collections.emptyList();
        }
        if (!Modifier.isPublic(module.getModifiers())) {
            logger.warning(String.format("Cannot access module %s", module));
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
                    bindings.add(new InstanceBinding(qualifier, bindFrom, bindTo, result));
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
        Optional<Class<?>> superModuleOpt = Arrays.stream(this.module.getInterfaces()).filter(c -> c.isAnnotationPresent(Module.class)).findFirst();

        while (superModuleOpt.isPresent()) {
            Class<?> superModule = superModuleOpt.get();
            bindings.addAll(getBindings(superModule));
            superModuleOpt = Arrays.stream(superModule.getInterfaces()).filter(c -> c.isAnnotationPresent(Module.class)).findFirst();
        }

        bindings.addAll(getBindings(this.module));
        Map<Key, Binding> bindingMap = new HashMap<>();
        bindings.forEach(b -> bindingMap.put(b.getKey(), b));

        return new ArrayList<>(bindingMap.values());
    }
}
