package com.wuzj.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {
    private int code;
    private String message;
    private Object data;

    public static R ok() {
        return new R(200, "success", null);
    }

    public static R fail() {
        return new R(500, "fail", null);
    }
    public static R ok(String message) {
        return new R(200, message, null);
    }

    public static R fail(String message) {
        return new R(500, message, null);
    }
    public static R ok(String message,Object data) {
        return new R(200, message, data);
    }

    public static R fail(String message,Object data) {
        return new R(500, message, data);
    }
}
