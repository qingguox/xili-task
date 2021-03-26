package com.xlg.component.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public interface IntDescValue {
    String getDesc();
    int getValue();

    static <T extends Enum & IntDescValue> T fromValue(Class<T> clazz, int value, T defaultValue) {
        return (T) Arrays.stream(clazz.getEnumConstants()).filter((x) -> {
            return Objects.equals(((IntDescValue) x).getValue(), value);
        }).findFirst().orElse(defaultValue);
    }

    default Map<String, Object> toValueDescMap() {
        return ImmutableMap.of("value", this.getValue(), "desc", this.getDesc());
    }
}
