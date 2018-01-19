package com.wanjun.canalsync.model.response;


import com.wanjun.canalsync.util.JSONUtil;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-15
 */
public class Response<T> {
    private int code = 0;
    private String message = "SUCCESS";
    private T data;

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(T data) {
        this.data = data;
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(result);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONUtil.toJson(this);
    }
}
