package com.xlg.cms.api.controller.login;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.utils.CaptchaUtil;
import com.xlg.cms.api.utils.CookieUtils;
import com.xlg.component.common.UserToken;
import com.xlg.component.enums.AllStatusEnum;
import com.xlg.component.service.XlgUserService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-21
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${constant.cookies.prefix.user}")
    private String COOKIE_PREFIX_USER;

    @Value("${constant.cookies.prefix.role}")
    private String COOKIE_PREFIX_ROLE;
    @Autowired
    private XlgUserService xlgUserService;


    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String toLogin(Model model) {
        model.addAttribute("isCaptcha", true);
        return "/login";
    }

    /** 登陆
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Result login(HttpServletRequest request, String username, String password, String captcha, String rememberMe,
            String role, HttpServletResponse response) throws IOException {

        logger.info("username={}, password={}, ", username, password);
        // 判断账号密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(captcha)) {
            return Result.error("账户, 密码, 验证码为null");
        }
        if (!NumberUtils.isDigits(username)) {
            return Result.error("账户必须为自己的工号！并且为数字");
        }
        // 判断用户名，密码是否正确
        // 判断验证码是否正确
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");
        if (StringUtils.isEmpty(captcha) || StringUtils.isEmpty(sessionCaptcha)
                || !captcha.toUpperCase().equals(sessionCaptcha.toUpperCase())) {
            return Result.error("验证码不正确！！请重新填写");
        }
        session.removeAttribute("captcha");

        UserToken token = new UserToken();
        token.setUserName(username);
        // TODO md5 加密
        String passwordFromMd5 = password;
        passwordFromMd5 = DigestUtils.md5DigestAsHex(password.getBytes());
        AllStatusEnum hasUser = xlgUserService.hasUser(Long.parseLong(username), passwordFromMd5, Integer.parseInt(role));
        logger.info("hasUser={}", JSON.toJSONString(hasUser));
        if (hasUser == AllStatusEnum.UNKNOWN || hasUser == AllStatusEnum.TACH) {
            return Result.error(hasUser.thirdDesc);
        }

        token.setPassword(passwordFromMd5);
        // 判断是否自动登录
        if (rememberMe != null) {
            token.setRememberMe(true);
            String key = JSON.toJSONString(token);
            Cookie cookie = new Cookie(COOKIE_PREFIX_USER, username + "." + passwordFromMd5);
            cookie.setMaxAge(60 * 60 * 6);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            Cookie cookie2 = new Cookie(COOKIE_PREFIX_ROLE, role);
            cookie.setMaxAge(60 * 60 * 6);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie2);
            // TODO uuid生成器
            logger.info("key={}", key);
        } else {
            token.setRememberMe(false);
        }
        request.getSession().setAttribute("user", username); // 表示已经登陆
        request.getSession().setAttribute("role", role); // 表示已经登陆
        logger.info("user={}, role={}", username, role);
        return Result.ok("/");
    }


    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        session.removeAttribute("role");
        Cookie cookie = CookieUtils.removeCookie(request.getCookies(), COOKIE_PREFIX_USER);
        Cookie cookie2 = CookieUtils.removeCookie(request.getCookies(), COOKIE_PREFIX_ROLE);
        if (cookie != null) {
            response.addCookie(cookie);
        }
        if (cookie2 != null) {
            response.addCookie(cookie2);
        }
        response.setHeader("refresh", "3;./CookieRead");
        return "redirect:/login.html";
    }

    /**
     * 验证码图片
     */
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置响应头信息，通知浏览器不要缓存
        response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "-1");
        response.setContentType("image/jpeg");

        // 获取验证码
        String code = CaptchaUtil.getRandomCode();
        // 将验证码输入到session中，用来验证
        request.getSession().setAttribute("captcha", code);
        // 输出到web页面
        ImageIO.write(CaptchaUtil.genCaptcha(code), "jpg", response.getOutputStream());
    }


    /**
     * 处理错误页面
     */
    @GetMapping("/error1")
    public String error2(HttpServletRequest request) {
        Integer code = (Integer) request.getSession().getAttribute("code");
        String msg = (String) request.getSession().getAttribute("msg");
        String errorMsg = "好像出错了呢！";
        if (code == null || code == 404 || code == 401) {
            errorMsg = "页面找不到了！好像是去月球了~";
            if (StringUtils.isNotBlank(msg)) {
                errorMsg = msg;
            }
            request.getSession().setAttribute("msg", errorMsg);
        }
        System.out.println(code + errorMsg);
        return "error";
    }

    public static void main(String[] args) {
        String password = "3170211030";
        String s = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(s);
    }


}
