package net.nokok.draft;

import java.lang.reflect.Type;
import java.util.Objects;

public class ParameterizedTypeImpl implements java.lang.reflect.ParameterizedType {

    private Class<?> rawType;
    private Type[] typeArguments;
    private Type ownerType;

    public ParameterizedTypeImpl(Class<?> rawType, Type[] typeArguments) {
        this(rawType, typeArguments, null);
    }

    public ParameterizedTypeImpl(Class<?> rawType, Type[] typeArguments, Type ownerType) {
        this.rawType = Objects.requireNonNull(rawType);
        this.typeArguments = Objects.requireNonNull(typeArguments);
        this.ownerType = rawType.getDeclaringClass();
        if (this.rawType.getTypeParameters().length == 0) {
            throw new IllegalArgumentException(String.format("%s is not a parameterized type", rawType));
        }
        if (this.rawType.getTypeParameters().length != typeArguments.length) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.typeArguments;
    }

    @Override
    public Type getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }
}
