package com.xlg.component.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务用户进度子项表 实体对象
 */
public class XlgTaskUserProgressItem {
    /**
     * 主键ID
     */
    private long id;

    /**
     * 进度ID
     */
    private long progressId;

    /**
     * 指标ID
     */
    private long indicator;

    /**
     * 条件ID
     */
    private long conditionId;

    /**
     * 任务ID 冗余
     */
    private long taskId;

    /**
     * 用户ID 冗余
     */
    private long userId;

    /**
     * 指标完成值
     */
    private long actionValue;

    /**
     * 状态 [0:未知, 1:进行,中 2:已完成, 3: 未完成]
     */
    private int status;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 修改时间
     */
    private long updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProgressId() {
        return progressId;
    }

    public void setProgressId(long progressId) {
        this.progressId = progressId;
    }

    public long getIndicator() {
        return indicator;
    }

    public void setIndicator(long indicator) {
        this.indicator = indicator;
    }

    public long getConditionId() {
        return conditionId;
    }

    public void setConditionId(long conditionId) {
        this.conditionId = conditionId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getActionValue() {
        return actionValue;
    }

    public void setActionValue(long actionValue) {
        this.actionValue = actionValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
