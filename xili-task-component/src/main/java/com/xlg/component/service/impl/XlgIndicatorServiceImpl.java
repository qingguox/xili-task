package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgIndicatorDAO;
import com.xlg.component.model.XlgIndicator;
import com.xlg.component.service.XlgIndicatorService;


/*
 * 指标表 service实现
 */
@Lazy
@Service
public class XlgIndicatorServiceImpl implements XlgIndicatorService {
    @Resource
    private XlgIndicatorDAO xlgIndicatorDAO;

    @Override
    public int batchInsert(List<XlgIndicator> list) {
        return xlgIndicatorDAO.batchInsert(list);
    }

    @Override
    public List<XlgIndicator> getAllIndicators() {
        return xlgIndicatorDAO.getAllIndicators();
    }

}