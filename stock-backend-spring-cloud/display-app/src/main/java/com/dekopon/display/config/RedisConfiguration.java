package com.dekopon.display.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author dekopon
 * @since 2023/6/14 01:03
 */
@Configuration
public class RedisConfiguration {

    public final static String K_DATA_DAILY_PREFIX = "k_data::daily::";
    public final static String K_DATA_HOURLY_PREFIX = "k_data::hourly::";
    public final static String K_DATA_LATEST_PREFIX = "k_data::latest::open::";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.addClusterNode(new RedisNode("8.137.96.5", 6379));
        config.addClusterNode(new RedisNode("8.137.98.1", 6379));
        config.addClusterNode(new RedisNode("47.109.79.121", 6379));
        config.addClusterNode(new RedisNode("47.109.56.80", 6379));
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        RedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        RedissonClient redissonClient;
        // 1. 创建配置
        Config config = new Config();
        config.useClusterServers().addNodeAddress("redis://8.137.96.5:6379", "redis://8.137.98.1:6379", "redis://47.109.79.121:6379", "redis://47.109.56.80:6379");

        // 2. 根据Config创建出RedissonClient实例
        redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
