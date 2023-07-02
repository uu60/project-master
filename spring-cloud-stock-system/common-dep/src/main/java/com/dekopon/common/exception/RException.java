package com.dekopon.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dekopon
 * @since 2023/7/2 11:37
 */
@Getter
@AllArgsConstructor
public class RException extends RuntimeException {
    private int code;
    private String msg;
}
