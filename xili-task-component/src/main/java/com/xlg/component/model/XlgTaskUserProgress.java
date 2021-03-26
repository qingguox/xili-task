package com.xlg.component.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务用户进度表 实体对象
 */
public class XlgTaskUserProgress {
    /**
     * 进度ID
     */
    private long id;

    /**
     * 任务ID
     */
    private long taskId;

    /**
     * 用户ID
     */
    private long userId;

    /**
     * 表示用户完成条件的个数
     */
    private int finished;

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

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
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
