package com.dekopon.testspringsecurity.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author octopus
 * @since 2023/6/3 10:58
 */
@Component
public class MyRedisSerializer<T> implements org.springframework.data.redis.serializer.RedisSerializer<T> {

    final Gson gson;

    public MyRedisSerializer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        String json = gson.toJson(t);
        return json.getBytes();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        String json = new String(bytes);
        return gson.fromJson(json, new TypeToken<>() {
        });
    }

    public static void main(String[] args) {
        Test<String> stringTest = new Test<>();
        ParameterizedType genericSuperclass =
                (ParameterizedType) (stringTest.getClass().getGenericSuperclass());
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        System.out.println(actualTypeArguments[0]);
    }

    static class Test<T> {

    }
}
