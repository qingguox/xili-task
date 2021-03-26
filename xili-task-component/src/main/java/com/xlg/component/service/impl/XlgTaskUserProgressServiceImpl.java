package com.xlg.component.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
       return xlgTaskUserProgressDAO.updateStatus(unfinishedIds, taskId, UserProgressStatusEnum.UNFINISHED.getValue());
    }

}