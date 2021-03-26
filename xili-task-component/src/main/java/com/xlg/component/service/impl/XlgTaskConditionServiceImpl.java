package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgTaskConditionDAO;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.service.XlgTaskConditionService;


/*
 * 任务条件表x service实现
 */
@Lazy
@Service
public class XlgTaskConditionServiceImpl implements XlgTaskConditionService {
    @Resource
    private XlgTaskConditionDAO xlgTaskConditionDAO;

    @Override
    public int batchInsert(List<XlgTaskCondition> list) {
        return xlgTaskConditionDAO.batchInsert(list);
    }

    @Override
    public List<XlgTaskCondition> getByTaskId(long taskId) {
        return xlgTaskConditionDAO.getByTaskId(taskId);
    }

}