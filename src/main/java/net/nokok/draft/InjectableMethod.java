package net.nokok.draft;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InjectableMethod {
    private final Class<?> declaredClass;
    private final Method method;

    public InjectableMethod(Class<?> declaredClass, Method method) {
        this.declaredClass = declaredClass;
        this.method = Objects.requireNonNull(method);
    }

    public String getName() {
        return this.method.getName();
    }

    public Class<?> getDeclaredClass() {
        return declaredClass;
    }

    public Method getRawMethod() {
        return method;
    }

    public void invoke(Object receiver, Object... args) throws ReflectiveOperationException {
        this.method.setAccessible(true);
        if (receiver.getClass().equals(this.declaredClass)) {
            this.method.invoke(receiver, args);
        } else {
            Constructor<MethodHandles.Lookup> methodHandleCtor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            methodHandleCtor.setAccessible(true);
            MethodHandle methodHandle = methodHandleCtor.newInstance(declaredClass).in(declaredClass).unreflectSpecial(method, declaredClass).bindTo(receiver);
            try {
                methodHandle.invokeWithArguments(args);
            } catch (Throwable e) {
                throw new ReflectiveOperationException(this.toString() + " withArguments " + Arrays.toString(args), e);
            }
        }
    }

    public List<Key> getParameterTypeKeys() {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Type[] methodGenericParameterTypes = method.getGenericParameterTypes();
        List<Key> keys = new ArrayList<>();
        for (int i = 0; i < methodGenericParameterTypes.length; i++) {
            Type methodGenericParameterType = methodGenericParameterTypes[i];
            Key methodKey = Key.of(Arrays.asList(parameterAnnotations[i]), methodGenericParameterType);
            keys.add(methodKey);
        }
        return keys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectableMethod that = (InjectableMethod) o;
        return declaredClass.equals(that.declaredClass) &&
                method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaredClass, method);
    }

    @Override
    public String toString() {
        String annotations = Arrays.stream(this.method.getDeclaredAnnotations()).map(Annotation::annotationType).map(Class::getSimpleName).map(t -> "@" + t).collect(Collectors.joining(","));
        String access = Modifier.toString(this.method.getModifiers());
        String returnType = this.method.getReturnType().getTypeName();
        String arguments = Arrays.stream(this.method.getGenericParameterTypes()).map(Type::getTypeName).collect(Collectors.joining(","));
        return String.format("%s %s %s %s#%s(%s)", annotations, access, returnType, this.declaredClass.getSimpleName(), this.getName(), arguments);
    }
}
