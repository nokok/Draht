package net.nokok.draft;

import javax.inject.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;

class NamedImpl implements Named, Serializable {

    private final String name;

    public NamedImpl(String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Named) {
            Named that = (Named) o;
            return this.name.equals(that.value());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (127 * "value".hashCode()) ^ value().hashCode();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }
}
