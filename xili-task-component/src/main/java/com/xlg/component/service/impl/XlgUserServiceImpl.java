package com.xlg.component.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.common.Page;
import com.xlg.component.dao.XlgUserDAO;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgUserService;


/*
 * 用户表 service实现
 */
@Lazy
@Service
public class XlgUserServiceImpl implements XlgUserService {
    @Resource
    private XlgUserDAO xlgUserDAO;

    @Override
    public int batchInsert(List<XlgUser> list) {
        return xlgUserDAO.batchInsert(list);
    }

    @Override
    public String format(long createId) {
        XlgUser user = xlgUserDAO.getByUserId(createId);
        return Optional.ofNullable(user.getName()).orElse("");
    }

    @Override
    public List<XlgUser> getAllTaskByPage(Page page, XlgUser request) {
        int offset = (page.page - 1) * page.count;
        return xlgUserDAO.getAllTaskByPage(request, offset, page.count);
    }

    @Override
    public int delete(long id) {
       return xlgUserDAO.delete(id);
    }

    @Override
    public int update(XlgUser user) {
        return xlgUserDAO.update(user);
    }

    @Override
    public long formatNameToCreateId(String creator) {
        XlgUser user = xlgUserDAO.getByName(creator);
        return Optional.ofNullable(user.getUserId()).orElse(0L);
    }

}