package com.xlg.cms.api.dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.xlg.cms.api.model.UploadFile;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-04
 */
public class TaskSaveDTO {
    private long taskId;
    private String taskName;
    private String taskDesc;
    private long startTime;
    private long endTime;
    private UploadFile file;
    private UploadFile indFile;
    private List<ConditionInfo> conditionList;

    public static class ConditionInfo {
        @SuppressWarnings("checkstyle:VisibilityModifier")
        public String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public List<ConditionInfo> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<ConditionInfo> conditionList) {
        this.conditionList = conditionList;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public UploadFile getFile() {
        return file;
    }

    public void setFile(UploadFile file) {
        this.file = file;
    }

    public UploadFile getIndFile() {
        return indFile;
    }

    public void setIndFile(UploadFile indFile) {
        this.indFile = indFile;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
