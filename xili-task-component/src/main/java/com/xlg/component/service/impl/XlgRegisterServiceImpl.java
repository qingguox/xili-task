package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgRegisterDAO;
import com.xlg.component.model.XlgRegister;
import com.xlg.component.service.XlgRegisterService;


/*
 * 指标表注册 service实现
 */
@Lazy
@Service
public class XlgRegisterServiceImpl implements XlgRegisterService {
    @Resource
    private XlgRegisterDAO xlgRegisterDAO;

    @Override
    public int batchInsert(List<XlgRegister> list) {
        return xlgRegisterDAO.batchInsert(list);
    }

    @Override
    public void updateStatus(long taskId, long time, int status) {
        xlgRegisterDAO.updateStatus(taskId, time, status);
    }

}