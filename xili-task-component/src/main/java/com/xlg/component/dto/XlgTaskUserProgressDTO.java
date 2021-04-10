package com.xlg.component.dto;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 * 去完成，推到进度数据DTO
 */
public class XlgTaskUserProgressDTO {

    private long userId;
    private long taskId;
    private long indicator;
    // 完成值
    private long actionValue;
    private int actionDate;
    private long actionTime;
    private String extParams;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getIndicator() {
        return indicator;
    }

    public void setIndicator(long indicator) {
        this.indicator = indicator;
    }

    public long getActionValue() {
        return actionValue;
    }

    public void setActionValue(long actionValue) {
        this.actionValue = actionValue;
    }

    public int getActionDate() {
        return actionDate;
    }

    public void setActionDate(int actionDate) {
        this.actionDate = actionDate;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }
}
