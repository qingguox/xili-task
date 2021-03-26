package com.xlg.cms.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-16
 */
@SpringBootApplication(scanBasePackages = "com.xlg")
@ServletComponentScan
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
