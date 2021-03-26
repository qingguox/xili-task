package com.xlg.component.service;

import java.util.List;

import com.xlg.component.common.Page;
import com.xlg.component.model.XlgTask;


/*
 * 任务表 service接口
 */
public interface XlgTaskService {

    int batchInsert(List<XlgTask> list);

    long insert(XlgTask task);

    List<XlgTask> getAllTaskByPage(Page page, XlgTask request);

    void updateStatus(long taskId, long time, int status);

    int update(XlgTask task);
}