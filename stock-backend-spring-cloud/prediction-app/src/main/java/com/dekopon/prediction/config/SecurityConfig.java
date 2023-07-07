package com.dekopon.prediction.config;

import com.dekopon.common.security.client.config.SecurityClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author dekopon
 * @since 2023/7/8 15:24
 */
@Configuration
@Import(SecurityClientConfig.class)
public class SecurityConfig {
}
