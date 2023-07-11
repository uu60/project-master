package com.dekopon.prediction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dekopon
 * @since 2023/7/8 16:10
 */
@Configuration
public class UtilBeanConfig {
    @Bean
    public ThreadPoolExecutor pool() {
        return new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean("isoSdf")
    public SimpleDateFormat isoSdf() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    }
}
