package net.nokok.draft;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

class ProviderBinding extends SimpleBinding implements Binding {

    private final Provider<?> provider;

    public ProviderBinding(List<Annotation> annotations, Type bindFrom, Type bindTo, Provider<?> provider) {
        super(annotations, bindFrom, bindTo);
        this.provider = provider;
    }

    public Provider<?> getProvider() {
        return provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProviderBinding that = (ProviderBinding) o;
        return provider.equals(that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), provider);
    }

    @Override
    public String toString() {
        return String.format("Provider(%s) %s ", this.provider, super.toString());
    }
}
