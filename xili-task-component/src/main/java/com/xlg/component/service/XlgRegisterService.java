package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgRegister;

/*
 * 指标表注册 service接口
 */
public interface XlgRegisterService {

    int batchInsert(List<XlgRegister> list);

    void updateStatus(long taskId, long time, int value);
}