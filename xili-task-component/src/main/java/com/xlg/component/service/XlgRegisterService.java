package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgRegister;

/*
 * 指标表注册 service接口
 */
public interface XlgRegisterService {

    int batchInsert(List<XlgRegister> list);

    int updateStatus(long taskId, long time, int value);

    /** 查询任务注册表，根据用户完成时间 和任务id 和任务注册在线状态
     * @param taskId
     * @param actionTime
     * @param value
     * @return
     */
    XlgRegister getByTaskIdAndTimeAndStatus(long taskId, long actionTime, int value);
}