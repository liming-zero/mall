package com.atguigu.gulimall.seckill.feign.config;

import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class OptionalDecoder implements Decoder {
    final Decoder delegate;

    public OptionalDecoder(Decoder delegate) {
        Objects.requireNonNull(delegate, "Decoder must not be null. ");
        this.delegate = delegate;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        long start = System.currentTimeMillis();
        if (!isOptional(type)) {
            Object o = delegate.decode(response, type);
            long end = System.currentTimeMillis();
            System.out.println("OptionalDecoder执行时间:" + (end-start));
            return o;
        }
        if (response.status() == 404 || response.status() == 204) {
            return Optional.empty();
        }
        Type enclosedType = Util.resolveLastTypeParameter(type, Optional.class);
        return Optional.ofNullable(delegate.decode(response, enclosedType));
    }

    static boolean isOptional(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return parameterizedType.getRawType().equals(Optional.class);
    }
}
