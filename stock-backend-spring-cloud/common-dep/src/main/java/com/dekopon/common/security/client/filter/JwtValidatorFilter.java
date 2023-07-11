package com.dekopon.common.security.client.filter;

import com.dekopon.common.pojo.ObjR;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author dekopon
 * @since 2023/6/18 16:27
 */
@AllArgsConstructor
public class JwtValidatorFilter extends OncePerRequestFilter {

    JwtParser jwtParser;
    Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jws<Claims> claims = jwtParser.parseClaimsJws(token);
                String username = claims.getBody().getSubject();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority("ROLE_TEST"), new SimpleGrantedAuthority("TEST"))));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                String json = gson.toJson(ObjR.e(ObjR.Codes.INVALID_TOKEN, "Invalid token."));
                response.setContentType("application/json");
                response.getWriter().write(json);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}