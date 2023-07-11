package com.dekopon.common.pojo;

import lombok.Getter;

/**
 * @author dekopon
 * @since 2023/6/13 18:54
 */
@Getter
@Deprecated
public class ObjR {
    private Integer code = 0;
    private String msg;
    private Object data;

    public static ObjR ok() {
        ObjR ObjR = new ObjR();
        ObjR.msg = "success";
        return ObjR;
    }

    public static ObjR ok(String msg) {
        ObjR ObjR = new ObjR();
        ObjR.msg = msg;
        return ObjR;
    }

    public static ObjR e(int code) {
        ObjR ObjR = new ObjR();
        ObjR.code = code;
        return ObjR;
    }

    public static ObjR e(int code, String msg) {
        ObjR ObjR = new ObjR();
        ObjR.code = code;
        ObjR.msg = msg;
        return ObjR;
    }

    public ObjR data(Object data) {
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

        public static final int DATE_WRONG_FORMAT = 1;
    }
}
