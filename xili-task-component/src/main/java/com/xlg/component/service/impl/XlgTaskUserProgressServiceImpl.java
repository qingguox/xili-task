package com.xlg.component.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.xlg.component.dao.XlgTaskUserProgressDAO;
import com.xlg.component.enums.UserProgressStatusEnum;
import com.xlg.component.model.XlgTaskUserProgress;
import com.xlg.component.service.XlgTaskUserProgressService;


/*
 * 任务用户进度表 service实现
 */
@Lazy
@Service
public class XlgTaskUserProgressServiceImpl implements XlgTaskUserProgressService {
    @Resource
    private XlgTaskUserProgressDAO xlgTaskUserProgressDAO;

    @Override
    public int batchInsert(List<XlgTaskUserProgress> list) {
        return xlgTaskUserProgressDAO.batchInsert(list);
    }

    @Override
    public int updateStatus(long taskId) {
        List<XlgTaskUserProgress> userProgressList = xlgTaskUserProgressDAO.getByTaskId(taskId);
        List<Long> unfinishedIds = userProgressList.stream()
                .filter(curProgress -> curProgress.getStatus() == UserProgressStatusEnum.DOING.getValue())
                .map(XlgTaskUserProgress::getId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(unfinishedIds)) {
            return 0;
        }
       return xlgTaskUserProgressDAO.updateStatus(unfinishedIds, taskId, UserProgressStatusEnum.UNFINISHED.getValue());
    }

    @Override
    public long getUserFinishedByTaskId(long taskId, int status) {
        return xlgTaskUserProgressDAO.getUserFinishedByTaskId(taskId, status);
    }

    @Override
    public List<Long> getStatusByTaskId(long taskId, List<Integer> status) {
        List<XlgTaskUserProgress> userProgressList = xlgTaskUserProgressDAO.getByTaskId(taskId);
        List<Long> userIdList = userProgressList.stream().filter(cur -> status.contains(cur.getStatus()))
                .map(XlgTaskUserProgress::getUserId).distinct().collect(Collectors.toList());
        return ListUtils.emptyIfNull(userIdList);
    }

    @Override
    public List<XlgTaskUserProgress> getProgressListByUserId(Collection taskIds, long userId) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Lists.newArrayList();
        }
        List<XlgTaskUserProgress> progressList = xlgTaskUserProgressDAO.getByTaskIdAndUserId(taskIds, userId);
        return ListUtils.emptyIfNull(progressList);
    }

    @Override
    public XlgTaskUserProgress getByTaskIdAndUserId(long taskId, long userId) {
        List<XlgTaskUserProgress> progressList =
                xlgTaskUserProgressDAO.getByTaskIdAndUserId(Lists.newArrayList(taskId), userId);
        if (CollectionUtils.isEmpty(progressList)) {
            return null;
        }
        return progressList.get(0);
    }

    @Override
    public int updateStatus(long progressId, int status) {
        return xlgTaskUserProgressDAO.updateStatus(progressId, status);
    }

    @Override
    public int updateFinishedCount(long progressId, long finishedItemSize) {
        return xlgTaskUserProgressDAO.updateFinishedCount(progressId, finishedItemSize);
    }

}