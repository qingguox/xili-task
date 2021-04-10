package com.xlg.component.processor;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.enums.TaskType;
import com.xlg.component.model.XlgTask;
import com.xlg.component.service.XlgTaskService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 * 任务开始处理
 */
@Service
public class XlgTaskStartProcessor implements XlgTaskStatusChangedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XlgTaskStartProcessor.class);

    @Autowired
    private XlgTaskService xlgTaskService;

    @Override
    public boolean support(TaskType taskType) {
        return TaskType.TASK_START == taskType;
    }

    @Override
    public void process(MessageDTO dto) {
        long taskId = dto.getTaskId();
        long now = System.currentTimeMillis();
        //1. 任务状态变更
        List<XlgTask> taskByIds = xlgTaskService.getTaskByIds(Lists.newArrayList(taskId));
        if (CollectionUtils.isEmpty(taskByIds)) {
            logger.error("[XlgTaskStartProcessor] taskByIds is null, dto={}", JSON.toJSONString(dto));
            return;
        }
        XlgTask xlgTask = taskByIds.get(0);
        if (xlgTask.getId() != taskId || xlgTask.getStatus() != TaskStatusEnum.PENDING.getValue()) {
            logger.error("[XlgTaskStartProcessor] taskId={} is null, dto={}", taskId, JSON.toJSONString(dto));
            return;
        }
        int count = xlgTaskService.updateStatus(taskId, now, TaskStatusEnum.ONLINE.getValue());
        if (count <= 0) {
            logger.info("[XlgTaskStartProcessor] 修改失败, taskId={}, dto={}", taskId, JSON.toJSONString(dto));
            return;
        }
        logger.info("[XlgTaskStartProcessor] 修改成功, taskId={}", taskId);
    }
}
