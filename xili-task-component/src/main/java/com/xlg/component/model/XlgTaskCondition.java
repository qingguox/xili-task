package com.xlg.component.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务条件表x 实体对象
 */
public class XlgTaskCondition {
    /**
     * 条件ID
     */
    private long id;

    /**
     * 任务ID
     */
    private long taskId;

    /**
     * 指标ID
     */
    private long indicator;

    /**
     * 指标名称 冗余
     */
    private String indicatorName;

    /**
     * 完成的阈值(默认为1，需大于等于1)
     */
    private int threshold;

    /**
     * 状态 [1:在线 , 2:移除]
     */
    private int status;

    /**
     * 扩展字段
     */
    private String extParams;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
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

    public long getIndicator() {
        return indicator;
    }

    public void setIndicator(long indicator) {
        this.indicator = indicator;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
