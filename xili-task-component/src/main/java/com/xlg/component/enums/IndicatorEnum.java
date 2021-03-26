package com.xlg.component.enums;


import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public enum IndicatorEnum implements IntDescValue {

    UNKNOWN(0, "未知"),
    UPLOAD_WORK(1, "上传作业"),
    STUDENT_KNOWN(2, "学生通知");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String desc;

    IndicatorEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static IndicatorEnum fromValue(int value) {
        return EnumUtils.fromValue(IndicatorEnum.class, value, UNKNOWN);
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
