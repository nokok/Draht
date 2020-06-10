package net.nokok.draft.internal;

import net.nokok.draft.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotatedParameterizedTypeKey extends AnnotatedTypeKey implements Key {

    private final ParameterizedType parameterizedType;

    public AnnotatedParameterizedTypeKey(List<? extends Annotation> annotations, ParameterizedType clazz) {
        super(annotations, (Class<?>) clazz.getRawType());
        this.parameterizedType = clazz;
    }

    @Override
    public boolean isGenericTypeKey() {
        return true;
    }

    @Override
    public Type getKeyAsGenericType() {
        return this.parameterizedType;
    }

    @Override
    public String toString() {
        return "Key: " + getName();
    }

    @Override
    public String getName() {
        String annotations = getAnnotations().stream().map(Annotation::toString).collect(Collectors.joining(","));
        if (annotations.isEmpty()) {
            return this.parameterizedType.getTypeName();
        } else {
            return String.format("%s:%s", annotations, this.parameterizedType.getTypeName());
        }
    }
}
