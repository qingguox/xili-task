package com.xlg.component.enums;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-18
 */
public enum XlgTaskCache implements IntDescValue {

    TASK_REGISTER("task_register_"),
    ;

    private String desc;

    XlgTaskCache(String desc) {
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public int getValue() {
        return 0;
    }
}
