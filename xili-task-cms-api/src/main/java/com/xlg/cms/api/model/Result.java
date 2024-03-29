package com.xlg.cms.api.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-20
 */
public class Result extends HashMap<String, Object> {
    public Result() {
        put("code", 200);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Result error() {
        return error(500, "未知异常，请联系管理员");
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static Result error(String msg) {
        return error(500, msg);
    }

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static Result ok(Object msg) {
        Result r = new Result();
        r.put("msg", msg);
        return r;
    }


    public static Result ok(Map<String, Object> map) {
        Result r = new Result();
        r.putAll(map);
        return r;
    }

    public static Result ok(long code, Object map) {
        Result r = new Result();
        r.put("code", code);
        r.put("msg", map);
        return r;
    }

    public static Result ok() {
        return new Result();
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
