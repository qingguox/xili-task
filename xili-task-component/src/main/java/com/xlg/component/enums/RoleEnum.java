package com.xlg.component.enums;

import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-21
 */
public enum RoleEnum implements IntDescValue {

    UNKNOWN(0, "全部"),
    TEACHER(1, "老师"),
    STUDENT(2, "学生"),
    MANAGER(3, "管理员");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String desc;

    RoleEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static RoleEnum valueOfDesc(String desc) {
        return EnumUtils.valueOf(RoleEnum.class, desc, UNKNOWN);
    }

    public static RoleEnum fromValue(int value) {
        return EnumUtils.fromValue(RoleEnum.class, value, UNKNOWN);
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
