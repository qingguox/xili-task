package com.xlg.cms.api.model;

import java.util.List;

import com.xlg.cms.api.dto.TaskSaveDTO.ConditionInfo;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-20
 */
public class TaskShow {
    private long taskId;
    private String taskName;
    private String taskDesc;
    private String status;
    private String creator;
    private String startTime;
    private String endTime;
    private String createTime;
    private UploadFile file;
    private UploadFile indFile;
    private List<ConditionInfo> conditionList;

    public TaskShow() {
    }

    public TaskShow(long taskId, String taskName, String taskDesc, String status, String creator,
            String startTime, String endTime, String createTime, UploadFile file, UploadFile indFile,
            List<ConditionInfo> conditionList) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.status = status;
        this.creator = creator;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.file = file;
        this.indFile = indFile;
        this.conditionList = conditionList;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public List<ConditionInfo> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<ConditionInfo> conditionList) {
        this.conditionList = conditionList;
    }
}
