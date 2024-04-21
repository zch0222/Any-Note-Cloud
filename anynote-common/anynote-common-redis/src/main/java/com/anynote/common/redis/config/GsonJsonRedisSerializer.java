package com.anynote.common.redis.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * Redis使用Gson序列化
 *
 * @author ruoyi
 */
public class GsonJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;
    private Gson gson;

    public GsonJsonRedisSerializer(Class<T> clazz)
    {
        super();
        this.clazz = clazz;
        this.gson = new GsonBuilder()
                .serializeNulls()
                .create();
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return gson.toJson(t).getBytes(DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing object to JSON", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            String str = new String(bytes, DEFAULT_CHARSET);
            return gson.fromJson(str, clazz);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing JSON to object", e);
        }
    }
}
