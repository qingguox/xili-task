package com.xlg.component.enums;

import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 */
public enum TaskType implements IntDescValue {

    UNKNOWN(0, ""),
    TASK_START(1, "任务开始"),
    TASK_END(2, "任务结束");

    public final int value;
    public final String desc;

    TaskType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TaskType fromValue(int value) {
        return EnumUtils.fromValue(TaskType.class, value, UNKNOWN);
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public int getValue() {
        return value;
    }
}
