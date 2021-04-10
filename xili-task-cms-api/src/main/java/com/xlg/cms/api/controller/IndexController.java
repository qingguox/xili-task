package com.xlg.cms.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xlg.component.service.XlgUserService;

@Controller
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private XlgUserService xlgUserService;

    /**
     * 页面跳转
     * @param
     * @return
     */
    @RequestMapping("/")
    public String page(Model model) {
        model.addAttribute("isCaptcha", true);
        return "main";
    }

    /**
     * 页面跳转
     * @param url
     * @return
     */
    @RequestMapping("{url}.html")
    public String page(@PathVariable("url") String url, Model model) {
        model.addAttribute("isCaptcha", true);
        return url;
    }

    /**
     * 页面跳转(二级目录)
     * @param module
     * @param url
     * @param url
     * @return
     */
    @GetMapping("{module}/{url}.html")
    public String page(@PathVariable("module") String module,
            @PathVariable("url") String url,
            Model model, HttpServletRequest request) {
        return module + "/" + url;
    }
}