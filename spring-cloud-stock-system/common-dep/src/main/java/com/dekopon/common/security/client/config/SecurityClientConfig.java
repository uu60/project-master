package com.dekopon.common.security.client.config;

import com.dekopon.common.security.client.filter.JwtValidatorFilter;
import com.dekopon.common.utils.Constants;
import com.google.gson.Gson;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author dekopon
 * @since 2023/6/17 19:02
 */
@Configuration
@EnableWebSecurity
@SuppressWarnings("all")
public class SecurityClientConfig {

    @Autowired
    Gson gson;

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parser().setSigningKey(Constants.JWT_SECRET);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/error/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtValidatorFilter(jwtParser(), gson), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
