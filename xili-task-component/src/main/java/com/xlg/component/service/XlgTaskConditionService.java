package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgTaskCondition;

/*
 * 任务条件表x service接口
 */
public interface XlgTaskConditionService {

    int batchInsert(List<XlgTaskCondition> list);

    List<XlgTaskCondition> getByTaskId(long id);
}