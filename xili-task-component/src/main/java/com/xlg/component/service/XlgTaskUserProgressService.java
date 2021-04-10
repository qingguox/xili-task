package com.xlg.component.service;

import java.util.Collection;
import java.util.List;

import com.xlg.component.model.XlgTaskUserProgress;

/*
 * 任务用户进度表 service接口
 */
public interface XlgTaskUserProgressService {

    int batchInsert(List<XlgTaskUserProgress> list);

    int updateStatus(long taskId);

    long getUserFinishedByTaskId(long taskId, int status);

    List<Long> getStatusByTaskId(long taskId, List<Integer> status);

    List<XlgTaskUserProgress> getProgressListByUserId(Collection taskIds, long userId);
}