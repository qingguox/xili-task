package com.xlg.cms.api.model;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-22
 */
public class StudentTask extends TeacherTask {

    private int userFinished;

    public int getUserFinished() {
        return userFinished;
    }

    public void setUserFinished(int userFinished) {
        this.userFinished = userFinished;
    }
}
