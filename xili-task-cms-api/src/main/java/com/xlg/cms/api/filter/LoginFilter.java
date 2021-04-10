package com.xlg.cms.api.filter;

import static com.xlg.component.enums.RoleEnum.MANAGER;
import static com.xlg.component.enums.RoleEnum.STUDENT;
import static com.xlg.component.enums.RoleEnum.TEACHER;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xlg.cms.api.utils.CookieUtils;
import com.xlg.component.service.XlgUserService;

/**
 * 注册器名称为customFilter,拦截的url为所有
 */
@WebFilter(filterName = "customFilter", urlPatterns = {"/*"})
public class LoginFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static Set<String> SUPPORT_URLS = Sets.newHashSet("task", "teacher", "manager", "student");
    private static Set<String> NOT_SUPPORT_URLS =
            Sets.newHashSet("/login.html", "/logout", "/error", "/error/", "/captcha", "/login", "/js/**",
                    "/html/**", "/image/**", "/css/**", "/rmqsend", "/file");
    private static Map<Integer, List<String>> roleUrlMap = Maps.newHashMap();

    static {
        roleUrlMap.put(TEACHER.value, Lists.newArrayList("/teacher*"));
        roleUrlMap.put(STUDENT.value, Lists.newArrayList("/student*"));
        roleUrlMap.put(MANAGER.value, Lists.newArrayList("/manager*"));
    }

    @Value("${constant.cookies.prefix.user}")
    private String COOKIE_PREFIX_USER;

    @Value("${constant.cookies.prefix.role}")
    private String COOKIE_PREFIX_ROLE;

    @Autowired
    private XlgUserService xlgUserService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("filter 初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        logger.info("customFilter 请求处理之前----doFilter方法之前过滤请求");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        //        if (NOT_SUPPORT_URLS.contains(path)) {
        //            chain.doFilter(request, response);
        //            return ;
        //        }
        //        String[] split = path.split("/");
        //        System.out.println(Arrays.toString(split));
        //        System.out.println(path);
        //        path = split[1];
        //        System.out.println(path);
        //        boolean needUrl = SUPPORT_URLS.contains(path);
        if (!NOT_SUPPORT_URLS.contains(path)) {
            //            System.out.println("需要校验。=needUrl{},+" + needUrl + path);
            //对request、response进行一些预处理
            // 比如设置请求编码
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            // 1、判断当前用户是否已经登录
            Object user = request.getSession().getAttribute("user");
            Object role1 = request.getSession().getAttribute("role");
            logger.info("userName={}", user);
            if (user == null) {
                // 未登录
                // 2、判断autologin对应cookie 是否存在
                Cookie cookie = CookieUtils.findCookie(request
                        .getCookies(), COOKIE_PREFIX_USER);
                Cookie cookie2 = CookieUtils.findCookie(request
                        .getCookies(), COOKIE_PREFIX_ROLE);
                if (cookie != null) {
                    // 找到了自动登录cookie
                    String username = cookie.getValue().split("\\.")[0];// 如果用户名中文，需要解密
                    username = URLDecoder.decode(username, "utf-8");

                    String password = cookie.getValue().split("\\.")[1];
                    logger.info("username={}, password={}", username, password);

                    int role = 0;
                    if (cookie2 != null) {
                        role = Integer.parseInt(cookie2.getValue());
                    }
                    // TODO 查询数据库是否存在用户
                    // 获得就是md5 加密密码，此处无需加密
                    if (username != null) {
                        request.getSession().setAttribute("user",
                                username);
                        request.getSession().setAttribute("role",
                                role);
                    }
                } else {
                    response.setHeader("refresh", "0;URL=/login.html");
                }
            }
        }
        chain.doFilter(request, response);
        logger.info("customFilter 请求处理之后----doFilter方法之后处理响应");
    }

    @Override
    public void destroy() {
        logger.info("filter 销毁");
    }
}