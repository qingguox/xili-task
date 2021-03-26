package com.xlg.component.enums;


import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 * 任务状态
 */
public enum TaskStatusEnum implements IntDescValue {

    UNKNOWN(0, "全部"),
    PENDING(1, "待进行"),
    ONLINE(2, "进行中"),
    OFFLINE(3, "已结束"),
    MANUAL_OFFLINE(4, "人工下线");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String desc;

    TaskStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static TaskStatusEnum fromValue(int value) {
       return EnumUtils.fromValue(TaskStatusEnum.class, value, UNKNOWN);
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
