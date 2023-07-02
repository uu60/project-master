package com.dekopon.display;

import com.dekopon.common.security.client.config.SecurityClientConfig;
import com.dekopon.common.security.client.filter.JwtValidatorFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SecurityClientConfig.class)
public class DisplayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisplayApplication.class, args);
    }

}
