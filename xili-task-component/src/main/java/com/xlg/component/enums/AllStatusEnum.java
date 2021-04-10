package com.xlg.component.enums;


import com.xlg.component.utils.EnumUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public enum AllStatusEnum implements IntDescValue {

    UNKNOWN(0, "未知", "未知", "用户不存在"),
    TACH(1, "在线", "有效", "用户账号密码错误"),
    DETACH(2, "移除", "无效", "用户正常");

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final int value;
    public final String desc;
    public final String secondDesc;
    public final String thirdDesc;

    AllStatusEnum(int value, String desc, String secondDesc, String thirdDesc) {
        this.value = value;
        this.desc = desc;
        this.secondDesc = secondDesc;
        this.thirdDesc = thirdDesc;
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
