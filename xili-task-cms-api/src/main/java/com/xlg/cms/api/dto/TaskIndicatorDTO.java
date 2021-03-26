package com.xlg.cms.api.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-05
 */
public class TaskIndicatorDTO {
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public int value;    // indicator
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public String desc;   // name

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
