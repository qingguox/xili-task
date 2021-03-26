package com.xlg.component.enums;


import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public enum UserProgressStatusEnum implements IntDescValue {

    UNKNOWN(0, "未知"),
    DOING(1, "进行中"),
    FINISHED(2, "已完成"),
    UNFINISHED(3, "未完成");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String desc;

    UserProgressStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserProgressStatusEnum fromValue(int value) {
        return EnumUtils.fromValue(UserProgressStatusEnum.class, value, UNKNOWN);
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
