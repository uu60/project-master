package com.dekopon.testspringsecurity.utils;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dekopon
 * @since 2023/6/3 11:06
 */
@Configuration
public class BeanConfiguration {
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
