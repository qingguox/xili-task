package com.xlg.cms.api.interceptor;

import static com.xlg.component.enums.RoleEnum.MANAGER;
import static com.xlg.component.enums.RoleEnum.STUDENT;
import static com.xlg.component.enums.RoleEnum.TEACHER;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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

    static {
        roleUrlMap.put(TEACHER.value, Lists.newArrayList("teacher", "task"));
        roleUrlMap.put(STUDENT.value, Lists.newArrayList("student", "task"));
        roleUrlMap.put(MANAGER.value, Lists.newArrayList("manager", "task", "teacher", "student"));
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
            role = (int)role1;
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
            request.getRequestDispatcher("/error").forward(request, response);
//            response.sendRedirect("/error");
            return false;
        }
        log.info("preHandle:请求前调用");
        //返回 false 则请求中断
        return true;
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