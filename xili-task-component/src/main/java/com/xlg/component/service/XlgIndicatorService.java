package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgIndicator;

/*
 * 指标表 service接口
 */
public interface XlgIndicatorService {

    int batchInsert(List<XlgIndicator> list);

    List<XlgIndicator> getAllIndicators();
}