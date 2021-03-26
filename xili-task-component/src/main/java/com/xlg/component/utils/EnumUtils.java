package com.xlg.component.utils;

import java.util.Arrays;
import java.util.Objects;

import com.xlg.component.enums.IntDescValue;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public class EnumUtils {

    public static <T extends Enum & IntDescValue> T fromValue(Class<T> clazz, int value, T defaultValue) {
        return (T) Arrays.stream(clazz.getEnumConstants()).filter((x) -> {
            return Objects.equals(((IntDescValue) x).getValue(), value);
        }).findFirst().orElse(defaultValue);
    }


    public static <T extends Enum & IntDescValue> T valueOf(Class<T> clazz, String desc, T defaultDesc) {
        return (T) Arrays.stream(clazz.getEnumConstants()).filter((x) -> {
            return Objects.equals(((IntDescValue) x).getDesc(), desc);
        }).findFirst().orElse(defaultDesc);
    }
}
