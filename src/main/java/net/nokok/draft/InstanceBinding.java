package net.nokok.draft;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class InstanceBinding extends SimpleBinding {
    private final Object instance;

    public InstanceBinding(List<Annotation> annotations, Type bindFrom, Type bindTo, Object instance) {
        super(annotations, bindFrom, bindTo);
        this.instance = instance;
    }

    public Object getRawValue() {
        return this.instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InstanceBinding that = (InstanceBinding) o;
        return Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instance);
    }

    @Override
    public String toString() {
        return String.format("Instance(%s) %s ", this.instance, super.toString());
    }
}
