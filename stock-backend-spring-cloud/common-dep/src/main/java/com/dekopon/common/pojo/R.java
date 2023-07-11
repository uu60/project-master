package com.dekopon.common.pojo;

import lombok.Getter;

@Getter
public class R<T> {
    public Integer code;
    public String msg;
    public T data;


    public static <U> R<U> ok() {
        R<U> r = new R<>();
        r.msg = "success";
        return r;
    }

    public static <U> R<U> ok(String msg) {
        R<U> r = new R<>();
        r.msg = msg;
        return r;
    }

    public static <U> R<U> e(int code) {
        R<U> r = new R<>();
        r.code = code;
        return r;
    }

    public static <U> R<U> e(int code, String msg) {
        R<U> r = new R<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }
}
