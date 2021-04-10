package com.xlg.cms.api.model;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 */
public class TaskToFinished {
    private long taskId;
    private int status;    // 指标id
    private UploadFile indFile;

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UploadFile getIndFile() {
        return indFile;
    }

    public void setIndFile(UploadFile indFile) {
        this.indFile = indFile;
    }
}
