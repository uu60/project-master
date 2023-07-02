package com.dekopon.display.utils;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author dekopon
 * @since 2023/7/2 12:20
 */
public class SecurityUtils {
    public static String getCurrentUsername() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
