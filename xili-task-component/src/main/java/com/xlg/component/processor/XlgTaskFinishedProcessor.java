package com.xlg.component.processor;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.AllStatusEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.enums.TaskType;
import com.xlg.component.model.XlgTask;
import com.xlg.component.service.XlgRegisterService;
import com.xlg.component.service.XlgTaskService;
import com.xlg.component.service.XlgTaskUserProgressService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 * 任务结束处理
 */
@Service
public class XlgTaskFinishedProcessor implements XlgTaskStatusChangedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XlgTaskFinishedProcessor.class);

    @Resource
    private XlgTaskService xlgTaskService;
    @Resource
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Resource
    private XlgRegisterService xlgRegisterService;

    @Override
    public boolean support(TaskType taskType) {
        return TaskType.TASK_END == taskType;
    }

    @Override
    public void process(MessageDTO dto) {
        // 1. 任务状态变更
        long taskId = dto.getTaskId();
        long now = System.currentTimeMillis();
        //1. 任务状态变更
        List<XlgTask> taskByIds = xlgTaskService.getTaskByIds(Lists.newArrayList(taskId));
        if (CollectionUtils.isEmpty(taskByIds)) {
            logger.error("[XlgTaskFinishedProcessor] taskByIds is null, dto={}", JSON.toJSONString(dto));
            return;
        }
        XlgTask xlgTask = taskByIds.get(0);
        if (xlgTask.getId() != taskId || xlgTask.getStatus() != TaskStatusEnum.ONLINE.getValue()) {
            logger.error("[XlgTaskFinishedProcessor] taskId={} is null, / status is not need ! task={}, dto={}", taskId,
                    JSON.toJSONString(xlgTask), JSON.toJSONString(dto));
            return;
        }
        int count = xlgTaskService.updateStatus(taskId, now, TaskStatusEnum.OFFLINE.getValue());
        if (count <= 0) {
            logger.info("[XlgTaskFinishedProcessor] 修改失败, taskId={}, dto={}", taskId, JSON.toJSONString(dto));
            return;
        }
        logger.info("[XlgTaskFinishedProcessor] 修改成功, taskId={}", taskId);

        // 2. 进度变更， 进行中-> 未完成
        // 找出任务下进行中的用户，然后修改状态 未完成
        xlgTaskUserProgressService.updateStatus(taskId);
        logger.info("[XlgTaskFinishedProcessor] 修改进度成功, taskId={}", taskId);

        // 3. 监控无效
        now = System.currentTimeMillis();
        count = xlgRegisterService.updateStatus(taskId, now, AllStatusEnum.DETACH.getValue());
        if (count <= 0) {
            logger.info("[XlgTaskFinishedProcessor] 修改监控无效, taskId={}, dto={}", taskId, JSON.toJSONString(dto));
            return;
        }
        logger.info("[XlgTaskFinishedProcessor] 修改监控成功, taskId={}", taskId);
    }
}
