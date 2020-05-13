package net.nokok.inject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeKey<T> extends Key<T> {
    private final ParameterizedType parameterizedType;

    protected ParameterizedTypeKey(ParameterizedType type) {
        super(type);
        parameterizedType = type;
    }

    @Override
    public Type getType() {
        return this.parameterizedType.getRawType();
    }

    @Override
    public Type getRealType() {
        return this.parameterizedType;
    }

    @Override
    public String toString() {
        return String.format("ParameterizedKey(%s)", super.getType());
    }
}
