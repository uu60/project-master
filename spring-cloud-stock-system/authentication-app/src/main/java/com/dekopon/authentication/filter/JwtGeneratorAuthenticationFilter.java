package com.dekopon.authentication.filter;

import com.dekopon.authentication.vo.UsernamePasswordVO;
import com.dekopon.common.pojo.R;
import com.dekopon.common.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/6/18 21:00
 */
public class JwtGeneratorAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    Gson gson;

    public JwtGeneratorAuthenticationFilter(AuthenticationManager authenticationManager, Gson gson) {
        super.setAuthenticationManager(authenticationManager);
        this.gson = gson;
        setFilterProcessesUrl("/api/v1/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UsernamePasswordVO usernamePasswordVO = objectMapper.readValue(request.getInputStream(),
                    UsernamePasswordVO.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(usernamePasswordVO.getUsername(),
                    usernamePasswordVO.getPassword());
            return super.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) {
        String token = Jwts.builder().setSubject(authResult.getName()).claim("authorities",
                authResult.getAuthorities()).setIssuedAt(new Date()).setExpiration(Date.from(LocalDate.now().plusMonths(2).atStartOfDay(ZoneId.systemDefault()).toInstant())).signWith(SignatureAlgorithm.HS256, Constants.JWT_SECRET).compact();
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.getOutputStream().write(gson.toJson(R.e(R.Codes.USERNAME_OR_PASSWORD_INVALID, "Wrong " +
                "username or password.")).getBytes(StandardCharsets.UTF_8));
    }
}
