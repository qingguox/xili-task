package com.xlg.cms.api.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xlg.component.common.Page;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgUserService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-15
 */
@Service
public class AdminUtils {

    @Autowired
    private XlgUserService xlgUserService;

    public long AdminId(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        long curUserId = 0;
        if (user != null) {
            curUserId = Long.parseLong((String) user);
        }
        return curUserId;
    }

    /**
     * 校验当前用户是否能操作add del update
     */
    public boolean CheckAdmin(HttpServletRequest servletRequest, List<RoleEnum> adminList) {
        XlgUser request = new XlgUser();
        request.setUserId(AdminId(servletRequest));
        XlgUser user = xlgUserService.getAllTaskByPage(new Page(1, 1), request).stream().findFirst().orElse(null);
        if (user != null) {
            RoleEnum roleEnum = RoleEnum.fromValue(user.getType());
            return adminList.contains(roleEnum);
        } else {
            return false;
        }
    }
}
