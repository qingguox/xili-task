package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.xlg.component.common.Page;
import com.xlg.component.dao.XlgTaskDAO;
import com.xlg.component.model.XlgTask;
import com.xlg.component.service.XlgTaskService;


/*
 * 任务表 service实现
 */
@Lazy
@Service
public class XlgTaskServiceImpl implements XlgTaskService {
    @Resource
    private XlgTaskDAO xlgTaskDAO;

    @Override
    public int batchInsert(List<XlgTask> list) {
        return xlgTaskDAO.batchInsert(list);
    }

    @Override
    public long insert(XlgTask task) {
        return xlgTaskDAO.insert(task);
    }

    @Override
    public List<XlgTask> getAllTaskByPage(Page page, XlgTask request) {
        int offset = (page.page - 1) * page.count;
        return xlgTaskDAO.getAllTaskByPage(request, offset, page.count);
    }

    @Override
    public int updateStatus(long taskId, long time, int status) {
        return xlgTaskDAO.updateStatus(taskId, time, status);
    }

    @Override
    public int update(XlgTask task) {
        return xlgTaskDAO.update(task);
    }

    @Override
    public List<XlgTask> getTaskByIds(List<Long> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return Lists.newArrayList();
        }
        return ListUtils.emptyIfNull(xlgTaskDAO.getTaskByIds(taskIdList));
    }
}