package com.xlg.cms.api.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-15
 */
public class AdminUtils {

    public static long AdminId(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        long curUserId = 0;
        if (user != null) {
            curUserId = Long.parseLong((String) user);
        }
        return curUserId;
    }
}
