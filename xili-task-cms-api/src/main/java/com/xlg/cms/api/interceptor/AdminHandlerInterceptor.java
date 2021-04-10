package com.xlg.cms.api.interceptor;

import static com.xlg.component.enums.RoleEnum.MANAGER;
import static com.xlg.component.enums.RoleEnum.STUDENT;
import static com.xlg.component.enums.RoleEnum.TEACHER;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.http.MimeHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-26
 */
@Component
public class AdminHandlerInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AdminHandlerInterceptor.class);
    private static Map<Integer, List<String>> roleUrlMap = Maps.newHashMap();
    private static List<String> otherUrls =
            Lists.newArrayList("AdminLTE", "bootstrap", "libs", "layer", "common.js", "iview", "Ionicons", "files.css",
                    "font-awesome", "common");

    static {
        roleUrlMap.put(TEACHER.value, Lists.newArrayList("teacher", "task", "file"));
        roleUrlMap.put(STUDENT.value, Lists.newArrayList("student", "task", "file"));
        roleUrlMap.put(MANAGER.value, Lists.newArrayList("manager", "task", "teacher", "student", "file"));
    }

    @Value("${constant.cookies.prefix.user}")
    private String COOKIE_PREFIX_USER;

    @Value("${constant.cookies.prefix.role}")
    private String COOKIE_PREFIX_ROLE;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        String[] split = path.split("/");
        System.out.println(Arrays.toString(split));
        System.out.println(path);
        path = split[1];
        System.out.println(path);

        if (otherUrls.contains(path)) {
            return true;
        }
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 1、判断当前用户是否已经登录
        Object user1 = request.getSession().getAttribute("user");
        Object role1 = request.getSession().getAttribute("role");
        System.out.println(user1 + "  " + role1);
        if (role1 == null) {
            return true;
        }
        int role = 0;
        if (role1 instanceof Integer) {
            role = (int) role1;
        }
        if (role1 instanceof String) {
            role = Integer.parseInt((String) role1);
        }
        List<String> urls = roleUrlMap.get(role);
        if (CollectionUtils.isEmpty(urls)) {
            log.info("role={}  is not exist!", role);
            return false;
        }
        if (!urls.contains(path)) {
            log.info("username={}, role={}, path={}, is not admin", user1, role, path);
            request.getSession().setAttribute("code", 401);
            request.getSession().setAttribute("msg", "没有权限!");
            reflectSetparam(request, "Request Method", "get");
            request.getRequestDispatcher("/error").forward(request, response);
            return false;
        }
        log.info("preHandle:请求前调用");
        //返回 false 则请求中断
        return true;
    }

    private void reflectSetparam(HttpServletRequest request, String key, String value) {
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        System.out.println("request实现类=" + requestClass.getName());
        try {
            Field request1 = requestClass.getDeclaredField("request");
            request1.setAccessible(true);
            Object o = request1.get(request);
            Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
            coyoteRequest.setAccessible(true);
            Object o1 = coyoteRequest.get(o);
            System.out.println("coyoteRequest实现类=" + o1.getClass().getName());
            Field headers = o1.getClass().getDeclaredField("headers");
            headers.setAccessible(true);
            MimeHeaders o2 = (MimeHeaders) headers.get(o1);
            o2.addValue(key).setString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        log.info("postHandle:请求后调用");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.info("afterCompletion:请求调用完成后回调方法，即在视图渲染完成后回调");
    }

}
