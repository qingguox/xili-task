package com.xlg.component.enums;


import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public enum AllStatusEnum implements IntDescValue {

    UNKNOWN(0, "未知", "未知"),
    TACH(1, "在线", "有效"),
    DETACH(2, "移除", "无效");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String desc;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String secondDesc;

    AllStatusEnum(int value, String desc, String secondDesc) {
        this.value = value;
        this.desc = desc;
        this.secondDesc = secondDesc;
    }

    public static AllStatusEnum fromValue(int value) {
        return EnumUtils.fromValue(AllStatusEnum.class, value, UNKNOWN);
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
