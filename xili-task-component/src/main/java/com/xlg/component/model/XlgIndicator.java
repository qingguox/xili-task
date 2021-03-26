package com.xlg.component.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 指标表 实体对象
 */
public class XlgIndicator {
    /**
     * 自增ID
     */
    private long id;

    /**
     * 监测指标ID
     */
    private long indicator;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 是否是上传文件 1 是 0 不是
     */
    private int type;

    /**
     * 指标内容
     */
    private String content;

    /**
     * 扩展字段
     */
    private String extParams;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 创建时间
     */
    private long updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIndicator() {
        return indicator;
    }

    public void setIndicator(long indicator) {
        this.indicator = indicator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
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
