package net.nokok.draft.internal;

import net.nokok.draft.InjectableMethod;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class MethodWrapper {

    private final Class<?> declaringClass;
    private final Method method;

    private MethodWrapper(Class<?> declaringClass, Method method) {
        this.declaringClass = declaringClass;
        this.method = method;
    }

    public static MethodWrapper of(Class<?> declaringClass, Method method) {
        return new MethodWrapper(declaringClass, method);
    }

    public static MethodWrapper of(InjectableMethod method) {
        return new MethodWrapper(method.getDeclaredClass(), method.getRawMethod());
    }

    public boolean hasInjectAnnotation() {
        return this.method.isAnnotationPresent(Inject.class);
    }

    public boolean hasNoInjectAnnotation() {
        return !this.hasInjectAnnotation();
    }

    public boolean isSynthetic() {
        return this.method.isSynthetic();
    }

    public boolean isBridge() {
        return this.method.isBridge();
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.method.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.method.getModifiers());
    }

    public boolean isPackagePrivate() {
        return !this.isPublic() && !this.isProtected() && !this.isPrivate();
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.method.getModifiers());
    }

    public String getDeclaringClassPackageName() {
        return this.method.getDeclaringClass().getPackageName();
    }

    public MethodSignature asSignature() {
        return new MethodSignature(this.method.getName(), Arrays.asList(this.method.getParameterTypes()));
    }

    public InjectableMethod toInjectable() {
        return new InjectableMethod(declaringClass, this.method);
    }

    public boolean isOverriding(Class<?> superClass) {
        try {
            MethodWrapper m = MethodWrapper.of(declaringClass, superClass.getDeclaredMethod(this.method.getName(), this.method.getParameterTypes()));
            if (m.isPrivate()) {
                return false;
            }
            if (m.isPackagePrivate() && !this.getDeclaringClassPackageName().equals(m.getDeclaringClassPackageName())) {
                return false;
            }
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public boolean isSamePackage(MethodWrapper m) {
        return this.getDeclaringClassPackageName().equals(m.getDeclaringClassPackageName());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.method.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.method.getModifiers());
    }

    public boolean isNotOverridable() {
        return this.isPrivate() || this.isFinal();
    }

    public boolean isAlreadyInjectedByChildren(Class<?> childrenClass) {
        MethodWrapper methodWrapper = new MethodWrapper(childrenClass, this.method);
        if (methodWrapper.hasNoInjectAnnotation()) {
            return false;
        }
        if (methodWrapper.isStatic()) {
            return false;
        }
        boolean overriding = methodWrapper.isOverriding(this.declaringClass);
        return overriding;
    }

    @Override
    public String toString() {
        return String.format("MethodW %s#%s(%s)", this.declaringClass.getSimpleName(), this.method.getName(), Arrays.toString(this.method.getGenericParameterTypes()));
    }
}
