package com.atguigu.gulimall.product.feign.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;

public class JacksonDecoder implements Decoder {

    private final ObjectMapper mapper;

    public JacksonDecoder() {
        this((Iterable) Collections.emptyList());
    }

    public JacksonDecoder(Iterable<Module> modules) {
        this((new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModules(modules));
    }

    public JacksonDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.status() == 404) {
            return Util.emptyValueOf(type);
        } else if (response.body() == null) {
            return null;
        } else {
            Reader reader = response.body().asReader();
            if (!((Reader)reader).markSupported()) {
                reader = new BufferedReader((Reader)reader, 1);
            }

            try {
                ((Reader)reader).mark(1);
                if (((Reader)reader).read() == -1) {
                    return null;
                } else {
                    ((Reader)reader).reset();
                    return this.mapper.readValue((Reader)reader, this.mapper.constructType(type));
                }
            } catch (RuntimeJsonMappingException var5) {
                if (var5.getCause() != null && var5.getCause() instanceof IOException) {
                    throw (IOException)IOException.class.cast(var5.getCause());
                } else {
                    throw var5;
                }
            }
        }
    }
}
