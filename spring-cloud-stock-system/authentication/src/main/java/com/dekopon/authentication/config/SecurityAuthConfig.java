package com.dekopon.authentication.config;

import com.dekopon.authentication.entity.AccountEntity;
import com.dekopon.authentication.filter.JwtGeneratorAuthenticationFilter;
import com.dekopon.authentication.service.AccountService;
import com.dekopon.authentication.vo.AccountUserDetails;
import com.dekopon.common.utils.Constants;
import com.google.gson.Gson;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author dekopon
 * @since 2023/6/17 19:02
 */
@Configuration
@EnableWebSecurity
public class SecurityAuthConfig {

    @Autowired
    AccountService accountService;
    @Autowired
    Gson gson;
    @Autowired
    AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public JwtParser jwtParser() {
        return Jwts.parser().setSigningKey(Constants.JWT_SECRET);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("POST", "/api/v1/register").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtGeneratorAuthenticationFilter(authenticationManager(), gson));
        return httpSecurity.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            AccountEntity accountEntity = accountService.getAccountEntity(username);
            if (accountEntity == null) {
                throw new UsernameNotFoundException("User not found");
            }
            AccountUserDetails accountUserDetails = new AccountUserDetails();
            BeanUtils.copyProperties(accountEntity, accountUserDetails);
            return accountUserDetails;
        };
    }
}
