package com.xlg.component.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgTaskFinishDetailDao;
import com.xlg.component.model.XlgTaskFinishDetail;
import com.xlg.component.service.XlgTaskFinishDetailService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 */
@Lazy
@Service
public class XlgTaskFinishDetailServiceImpl implements XlgTaskFinishDetailService {

    @Autowired
    private XlgTaskFinishDetailDao xlgTaskFinishDetailDao;

    @Override
    public int batchInsert(List<XlgTaskFinishDetail> list) {
        return xlgTaskFinishDetailDao.batchInsert(list);
    }

    @Override
    public XlgTaskFinishDetail getTaskIdAndUserId(long taskId, long userId, long indicator) {
        return xlgTaskFinishDetailDao.getTaskIdAndUserId(taskId, userId, indicator);
    }

    @Override
    public int updateExtParams(XlgTaskFinishDetail xlgTaskFinishDetail) {
        return xlgTaskFinishDetailDao.updateExtParams(xlgTaskFinishDetail);
    }
}
