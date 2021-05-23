package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgTaskUserDAO;
import com.xlg.component.model.XlgTaskUser;
import com.xlg.component.service.XlgTaskUserService;


/*
 * 任务用户表 service实现
 */
@Lazy
@Service
public class XlgTaskUserServiceImpl implements XlgTaskUserService {
    @Resource
    private XlgTaskUserDAO xlgTaskUserDAO;

    @Override
    public int batchInsert(List<XlgTaskUser> list) {
        return xlgTaskUserDAO.batchInsert(list);
    }

    @Override
    public long getUserCountByTaskId(long taskId) {
        return xlgTaskUserDAO.getUserCountByTaskId(taskId);
    }

    @Override
    public List<XlgTaskUser> getUserByTaskId(long taskId) {
        return xlgTaskUserDAO.getUserByTaskId(taskId);
    }
}