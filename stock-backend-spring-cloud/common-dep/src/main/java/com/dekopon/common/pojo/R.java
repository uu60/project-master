package com.dekopon.common.pojo;

import lombok.Data;

/**
 * @author dekopon
 * @since 2023/6/13 18:54
 */
@Data
public class R {
    private Integer code = 0;
    private String msg;
    private Object data;

    public static R ok() {
        R R = new R();
        R.msg = "success";
        return R;
    }

    public static R ok(String msg) {
        R R = new R();
        R.msg = msg;
        return R;
    }

    public static R e(int code) {
        R R = new R();
        R.code = code;
        return R;
    }

    public static R e(int code, String msg) {
        R R = new R();
        R.code = code;
        R.msg = msg;
        return R;
    }

    public R setData(Object data) {
        this.data = data;
        return this;
    }
    
    public static class Codes {
        public static final int SUCCESS = 0;
        public static final int SERVER_INTERNAL_ERROR = -1;

        public static final int K_DATA_WAIT = 1;

        public static final int INVALID_TOKEN = 400;
        public static final int USERNAME_EXISTED = 1;
        public static final int USERNAME_OR_PASSWORD_WRONG_FORMAT = 2;
        public static final int USERNAME_OR_PASSWORD_INVALID = 3;
    }
}
