package com.xlg.component.dto;

import com.xlg.component.enums.TaskType;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-29
 */
public class MessageDTO {

    private long targetMills;
    private long taskId;
    private TaskType taskType;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getTargetMills() {
        return targetMills;
    }

    public void setTargetMills(long targetMills) {
        this.targetMills = targetMills;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
}
