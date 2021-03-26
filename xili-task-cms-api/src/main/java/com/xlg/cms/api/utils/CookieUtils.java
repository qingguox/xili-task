package com.xlg.cms.api.utils;

import javax.servlet.http.Cookie;

public class CookieUtils {


    // 在cookies 数组中 查找指定name 的cookie
    public static Cookie findCookie(Cookie[] cookies, String name) {
        if (cookies == null) {// cookie 不存在
            return null;
        } else {
            // cookie 存在
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    // 找到
                    return cookie;
                }
            }
            // 没有找到
            return null;
        }
    }

    public static Cookie removeCookie(Cookie[] cookies, String name) {
        if (cookies != null) {// cookie 不存在
            // cookie 存在
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    // 找到
                    cookie.setMaxAge(0);
                    return cookie;
                }
            }
        }
        return null;
    }
}