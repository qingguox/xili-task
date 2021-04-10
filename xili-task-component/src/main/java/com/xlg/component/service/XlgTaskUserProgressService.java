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

    XlgTaskUserProgress getByTaskIdAndUserId(long taskId, long userId);

    /**
     *  更新进度
     * @param progressId
     * @param status
     * @return
     */
    int updateStatus(long progressId, int status);

    /**
     * 更新完成item个数
     * @param progressId
     * @param finishedItemSize
     * @return
     */
    int updateFinishedCount(long progressId, long finishedItemSize);
}