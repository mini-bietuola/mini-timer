package com.netease.mini.bietuola.web.util;

import java.io.Serializable;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/4/28
 */
public class JsonResponse implements Serializable {

    private static final long serialVersionUID = -7642609102961823970L;
    private int code;
    private String msg;
    private Object data;

    JsonResponse(int code) {
        this(code, null, null);
    }

    JsonResponse(int code, Object data) {
        this(code, null, data);
    }

    JsonResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResponse success() {
        return new JsonResponse(ResultCode.SUCCESS);
    }

    public static JsonResponse success(Object data) {
        return new JsonResponse(ResultCode.SUCCESS, data);
    }

    public static JsonResponse codeOf(int code) {
        return new JsonResponse(code);
    }

    public int getCode() {
        return code;
    }

    public JsonResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public JsonResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public JsonResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
