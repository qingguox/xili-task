package com.xlg.cms.api.model;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-22
 */
public class TeacherTask extends TaskShow {
    private long taskUsers;
    private long taskFinished;
    private long taskUnFinished;

    public long getTaskUsers() {
        return taskUsers;
    }

    public void setTaskUsers(long taskUsers) {
        this.taskUsers = taskUsers;
    }

    public long getTaskFinished() {
        return taskFinished;
    }

    public void setTaskFinished(long taskFinished) {
        this.taskFinished = taskFinished;
    }

    public long getTaskUnFinished() {
        return taskUnFinished;
    }

    public void setTaskUnFinished(long taskUnFinished) {
        this.taskUnFinished = taskUnFinished;
    }
}
