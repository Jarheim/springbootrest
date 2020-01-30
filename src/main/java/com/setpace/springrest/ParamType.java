package com.setpace.springrest;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ParamType implements ParameterizedType {
    private ParameterizedType delegate;
    private Type[] actualTypeArguments;

    private ParamType(ParameterizedType delegate, Type[] actualTypeArguments) {
        this.delegate = delegate;
        this.actualTypeArguments = actualTypeArguments;
    }

    public static <T> ParameterizedTypeReference<List<T>> getParamTypeRef(Class<T> clazz) {
        return new ParameterizedTypeReference<List<T>>() {
            public Type getType() {
                return new ParamType((ParameterizedType) super.getType(), new Type[]{clazz});
            }
        };
    }

    public static <K, V> ParameterizedTypeReference<Map<K, V>> getMapTypeRef(Class<K> clazzK, Class<V> clazzV) {
        return new ParameterizedTypeReference<Map<K, V>>() {
            public Type getType() {
                return new ParamType((ParameterizedType) super.getType(), new Type[]{clazzK, clazzV});
            }
        };
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return delegate.getRawType();
    }

    @Override
    public Type getOwnerType() {
        return delegate.getOwnerType();
    }
}